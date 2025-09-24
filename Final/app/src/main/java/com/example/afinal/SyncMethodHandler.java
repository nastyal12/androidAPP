package com.example.afinal;

import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class SyncMethodHandler {

    private MainActivity mainActivity;

    // Конструктор для инициализации ссылающейся активности
    public SyncMethodHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    // Метод для выполнения синхронной операции (например, загрузка данных через Thread)
    public void handleSyncMethod() {
        // Устанавливаем использование синхронного метода
        mainActivity.setUseAsyncTask(false); // Ставим флаг, что используем синхронный метод

        // Запуск синхронной операции (используем Thread для загрузки данных)
        mainActivity.loadDataWithThread();  // Вызываем метод для загрузки данных через Thread

        Toast.makeText(mainActivity, "Sync Method selected: Data is being loaded synchronously.", Toast.LENGTH_SHORT).show();
    }

    // Метод для парсинга JSON в список сотрудников
    public List<Employee> parseJsonToEmployees(String jsonString) {
        List<Employee> employeeList = new ArrayList<>();

        try {
            JSONObject rootObject = new JSONObject(jsonString);

            // Обрати внимание на ключ "Employee" с большой буквы
            JSONArray employeesArray = rootObject.getJSONArray("Employee");

            for (int i = 0; i < employeesArray.length(); i++) {
                JSONObject employeeObject = employeesArray.getJSONObject(i);

                String id = employeeObject.getString("id");
                String name = employeeObject.getString("name");
                String gender = employeeObject.getString("gender");
                String image = employeeObject.getString("image");
                int salary = employeeObject.getInt("salary");

                Employee employee = new Employee(id, name, gender, salary, image);
                employeeList.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employeeList;
    }
}
