package com.example.cuisine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class DBConnector {
    public static String executeQuery(String... params) {
        String type = params[0];
        String result = "";
        if(type.equals("loadFoodData")){ //取得所有食物資訊
            try {
                String language = params[1];
                URL url=new URL("http://路徑/homePage.php");
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("language","UTF-8") + "=" + URLEncoder.encode(language, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
                inputStream.close();
                result = builder.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(type.equals("searchFood")){ //依據多個查詢條件取得食物資訊
            try {
                String language = params[1];
                String food_name = params[2];
                String food_vag = params[3];
                String food_spy = params[4];
                String food_type = params[5];
                String food_meat = params[6];
                URL url=new URL("http://路徑/searchPage.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("language","UTF-8") + "=" + URLEncoder.encode(language, "UTF-8") + "&" +
                        URLEncoder.encode("food_name","UTF-8") + "=" + URLEncoder.encode(food_name, "UTF-8") + "&" +
                        URLEncoder.encode("food_vag","UTF-8") + "=" + URLEncoder.encode(food_vag, "UTF-8")+ "&" +
                        URLEncoder.encode("food_spy","UTF-8") + "=" + URLEncoder.encode(food_spy, "UTF-8")+ "&" +
                        URLEncoder.encode("food_meat","UTF-8") + "=" + URLEncoder.encode(food_meat, "UTF-8")+ "&" +
                        URLEncoder.encode("food_type","UTF-8") + "=" + URLEncoder.encode(food_type, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
                inputStream.close();
                result = builder.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(type.equals("recipePage")){//依據食物名稱取得食譜
            try {
                String language = params[1];
                String food_id = params[2];
                URL url=new URL("http://路徑/recipePage.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("language","UTF-8") + "=" +URLEncoder.encode(language, "UTF-8") + "&" +
                        URLEncoder.encode("food_id","UTF-8") + "=" +URLEncoder.encode(food_id, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
                inputStream.close();
                result = builder.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(type.equals("findFoodById")){//依據食物ID取得食物資訊
            try {
                String language = params[1];
                String food_id = params[2];
                URL url=new URL("http://路徑/findFoodById.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("language","UTF-8") + "=" +URLEncoder.encode(language, "UTF-8") + "&" +
                        URLEncoder.encode("food_id","UTF-8") + "=" +URLEncoder.encode(food_id, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
                inputStream.close();
                result = builder.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
