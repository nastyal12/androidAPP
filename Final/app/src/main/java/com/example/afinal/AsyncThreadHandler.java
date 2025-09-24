package com.example.afinal;

import android.os.AsyncTask;
import android.widget.Toast;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AsyncThreadHandler {

    private MainActivity mainActivity;  // Ссылка на MainActivity, чтобы можно было обновлять UI

    public AsyncThreadHandler(MainActivity activity) {
        this.mainActivity = activity;
    }

    public void handleAsyncThread() {
        // Если используется AsyncTask, запускаем асинхронную задачу
        new LoadDataAsyncTask().execute();
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, List<Employee>> {
        private String errorMessage;

        @Override
        protected void onPreExecute() {
            // Показываем progress bar перед началом загрузки
            mainActivity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Employee> doInBackground(Void... voids) {
            try {
                // Загружаем данные
                String jsonString = downloadJson(mainActivity.JSON_URL);
                return parseJsonToEmployees(jsonString);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Employee> employees) {
            mainActivity.progressBar.setVisibility(View.GONE); // Скрываем прогрессбар

            if (employees != null) {
                // Сохраняем данные в базе
                mainActivity.dbHelper.clearAllEmployees();
                for (Employee employee : employees) {
                    mainActivity.dbHelper.addEmployee(employee);
                }
                // Загружаем сотрудников из базы
                mainActivity.loadEmployeesFromDatabase();
                Toast.makeText(mainActivity, "데이터 로드 완료", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mainActivity, "데이터 로드 실패: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }

        private String downloadJson(String urlString) throws IOException {
            // Скачиваем JSON данные с сервера
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            reader.close();
            inputStream.close();
            connection.disconnect();

            return stringBuilder.toString();
        }

        private List<Employee> parseJsonToEmployees(String jsonString) throws Exception {
            // Преобразуем JSON в список сотрудников
            List<Employee> employees = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray employeeArray = jsonObject.getJSONArray("Employee");

            for (int i = 0; i < employeeArray.length(); i++) {
                JSONObject employeeJson = employeeArray.getJSONObject(i);

                Employee employee = new Employee();
                employee.setId(employeeJson.getString("id"));
                employee.setName(employeeJson.getString("name"));
                employee.setGender(employeeJson.getString("gender"));
                employee.setSalary(employeeJson.getInt("salary"));
                employee.setImage(employeeJson.getString("image"));

                employees.add(employee);
            }

            return employees;
        }
    }
}
