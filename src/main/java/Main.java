import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.lang.reflect.Type;
import java.util.*;
import java.io.*;


public class Main {

    public static void main (String [] args) throws Exception {
       List <String []> list = new ArrayList<>();
       list.add(("1,John,Smith,USA,25".split(",")));
       list.add(("2,Inav,Petrov,RU,23".split(",")));



        try(CSVWriter writer = new CSVWriter(new FileWriter("data.csv",false))){
            writer.writeAll(list);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

        String [] strings = {"id" , "firstName", "lastName", "country", "age"};
        List <Employee> list1 = parseCSV( strings , "data.csv");

       String json =  listToJson(list1);
       writeString(json, "data.json");

       createXmlFile(list, "data.xml");
        //TODO форматирование xml файла при записи

        List<Employee> lastEmployeeList = parseXML("data.xml");
        String json2 =listToJson(lastEmployeeList);
        writeString(json2, "data2.json");

        readStringAndJsonToList("data2.json");

    }

    public static List<Employee> parseCSV(String [] columnMapping, String name){
        try(CSVReader csvReader = new CSVReader(new FileReader(name))){
            ColumnPositionMappingStrategy <Employee> strategy = new ColumnPositionMappingStrategy<>();

            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean <Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
           return csv.parse();

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String listToJson(List <Employee> staff){
        Type listType = new TypeToken<List<Employee>>() {}.getType();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.setPrettyPrinting().create();
          return   gson.toJson(staff,listType);

      }

      public static void writeString(String json, String name){

         try(FileWriter fileWriter = new FileWriter(name)){

                fileWriter.write(json);
                fileWriter.flush();

         }catch (IOException e){
             System.out.println(e.getMessage());
         }
      }

      public static void createXmlFile (List <String []> list ,String string) throws ParserConfigurationException,
              TransformerException {

          DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          DocumentBuilder builder = factory.newDocumentBuilder();
          Document document = builder.newDocument();// для записи xml файла

          Element staff = document.createElement("staff");
          document.appendChild(staff);
          for(String [] e : list){
              Element employee = document.createElement("employee");
              staff.appendChild(employee);
              Element id = document.createElement("id");
              id.appendChild(document.createTextNode(e[0]));
              employee.appendChild(id);
              Element firstName = document.createElement("firstName");
              firstName.appendChild(document.createTextNode(e[1]));
              employee.appendChild(firstName);
              Element lastName = document.createElement("lastName");
              lastName.appendChild(document.createTextNode(e[2]));
              employee.appendChild(lastName);
              Element country = document.createElement("country");
              country.appendChild(document.createTextNode(e[3]));
              employee.appendChild(country);
              Element age = document.createElement("age");
              age.appendChild(document.createTextNode(e[4]));
              employee.appendChild(age); // сама запись
              System.out.println(document);
          }
          DOMSource domSource =  new DOMSource(document);// что будет закидывать
          StreamResult streamResult = new StreamResult(new File(string));
          TransformerFactory transformerFactory = TransformerFactory.newInstance();
          Transformer transformer = transformerFactory.newTransformer();
          transformer.transform(domSource, streamResult);
      }

      public static List<Employee> parseXML (String string)throws Exception{
        int id = 0;
        String firstName = null;
        String lastName = null;
        String county = null;
        int age = 0;

        List <Employee> employees = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(string));
        Node staff = document.getDocumentElement();//получаем корневой узел
        NodeList nodeList = staff.getChildNodes();//получаем дочерние узлы корневого узла

        for(int i = 0; i < nodeList.getLength(); i ++){
            Node node = nodeList.item(i);// получаем объекты вложенные в корневой узел
            if(Node.ELEMENT_NODE == node.getNodeType()){
                Element element = (Element) node;// приводим к нужному типу
                id = Integer.valueOf(element.getElementsByTagName("id").item(0).getTextContent());
                firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                county = element.getElementsByTagName("country").item(0).getTextContent();
                age = Integer.valueOf(element.getElementsByTagName("age").item(0).getTextContent());
                employees.add(new Employee(id,firstName,lastName, county,age ));

            }

        }
        return employees;
      }

      public static void readStringAndJsonToList(String string) {
        JSONParser parser = new JSONParser();
        List <Employee> employees = new ArrayList<>();

        try{
            Object obj = parser.parse(new FileReader(string));
            JSONArray jsonArray = (JSONArray) obj;// получаю массив данных формата json
            //String newArray = jsonArray.toJSONString();//перевожу в string
            GsonBuilder builder = new GsonBuilder();// создаю builder
            Gson gson = builder.create();

            jsonArray.forEach(s -> {
                JSONObject sObj = (JSONObject) s;
                Employee employee = gson.fromJson(String.valueOf(sObj), Employee.class);
                employees.add(employee);
            } );
            System.out.println(employees);
        }catch (IOException | ParseException e){
            System.out.println(e.getMessage());
        }
      }
}




