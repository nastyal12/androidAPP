package com.example.afinal;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String JSON_URL ="http://10.0.2.2:8082/sawon.json";
    private ListView listViewEmployees;
    private EmployeeAdapter adapter;
    DBHelper dbHelper;
    ProgressBar progressBar;

    private Button btnAll, btnMale, btnFemale, btnSortAsc, btnSortDesc;

    private String currentFilter = "all"; // all, male, female
    private boolean currentSort = false; // false = desc, true = asc
    private boolean useAsyncTask = false; // false = Thread, true = AsyncTask
    private boolean useCircularImage = false; // false = rectangular, true = circular
    private boolean backgroundMusicEnabled = true;
    private MediaPlayer mediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initDatabase();

        initViews();
        setupListeners();

        // Загрузка данных
        loadEmployeeData();

        // Инициализация фоновой музыки
        initBackgroundMusic();
    }

    private void initViews() {
        listViewEmployees = findViewById(R.id.listview_employees);
        progressBar = findViewById(R.id.progressbar);

        btnAll = findViewById(R.id.btn_all);
        btnMale = findViewById(R.id.btn_male);
        btnFemale = findViewById(R.id.btn_female);
        btnSortAsc = findViewById(R.id.btn_sort_asc);
        btnSortDesc = findViewById(R.id.btn_sort_desc);

        adapter = new EmployeeAdapter(this, new ArrayList<Employee>());
        listViewEmployees.setAdapter(adapter);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this);
    }

    private void setupListeners() {
        // Filter buttons
        btnAll.setOnClickListener(v -> filterEmployees("all"));
        btnMale.setOnClickListener(v -> filterEmployees("남"));
        btnFemale.setOnClickListener(v -> filterEmployees("여"));

        // Sort buttons
        btnSortAsc.setOnClickListener(v -> sortEmployees(true));
        btnSortDesc.setOnClickListener(v -> sortEmployees(false));

        // List item click listener
        listViewEmployees.setOnItemClickListener((parent, view, position, id) -> {
            Employee employee = (Employee) adapter.getItem(position);
            showEmployeeDialog(employee);
        });
    }

    private void loadEmployeeData() {
        if (dbHelper.isEmpty()) {
            // Если база данных пустая, загружаем данные
            if (useAsyncTask) {
                new AsyncThreadHandler(this).handleAsyncThread(); // Передаем MainActivity в handler
            } else {
                loadDataWithThread();
            }
        } else {
            loadEmployeesFromDatabase();
        }
    }

    void loadEmployeesFromDatabase() {
        List<Employee> employees;

        if (currentFilter.equals("all")) {
            employees = dbHelper.getEmployeesSortedBySalary(!currentSort);
        } else {
            employees = dbHelper.getEmployeesByGenderSortedBySalary(currentFilter, !currentSort);
        }

        adapter.updateEmployees(employees);
        updateButtonStates();
    }

    private void filterEmployees(String filter) {
        currentFilter = filter;
        loadEmployeesFromDatabase();
    }

    private void sortEmployees(boolean ascending) {
        currentSort = ascending;
        loadEmployeesFromDatabase();
    }

    private void updateButtonStates() {
        // Reset all button colors
        btnAll.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnMale.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnFemale.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnSortAsc.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnSortDesc.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        // Highlight active filter button
        if (currentFilter.equals("all")) {
            btnAll.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (currentFilter.equals("남")) {
            btnMale.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        } else if (currentFilter.equals("여")) {
            btnFemale.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        // Highlight active sort button
        if (currentSort) {
            btnSortAsc.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            btnSortDesc.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void showEmployeeDialog(Employee employee) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_employee_detail, null);

        ImageView imgEmployee = dialogView.findViewById(R.id.img_employee_large);
        ImageView imgQRCode = dialogView.findViewById(R.id.img_qr_code);

        // Load employee image
        Glide.with(this)
                .load(employee.getImage())
                .into(imgEmployee);

        // Generate and display QR code
        Bitmap qrBitmap = QRCodeGenerator.generateEmployeeQRCode(employee);
        if (qrBitmap != null) {
            imgQRCode.setImageBitmap(qrBitmap);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(employee.getName() + " (" + employee.getId() + ")")
                .setView(dialogView)
                .setPositiveButton("확인", null)
                .show();
    }
    public void setImageShape(boolean isCircular) {
        useCircularImage = isCircular;
        adapter.setCircularImage(useCircularImage); // Обновляем адаптер
    }

    private void initBackgroundMusic() {
        if (backgroundMusicEnabled) {
            try {
                mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Создаем экземпляры классов для обработки действий
        AsyncThreadHandler asyncThreadHandler = new AsyncThreadHandler(this); // Передаем ссылку на MainActivity
        SyncMethodHandler syncMethodHandler = new SyncMethodHandler(this);
        ImageShapeHandler imageShapeHandler = new ImageShapeHandler(this);

        if (id == R.id.menu_background_music) {
            toggleBackgroundMusic(); // Пример метода для переключения фона музыки
            return true;
        } else if (id == R.id.menu_communication_method) {
            showCommunicationMethodDialog();
            return true;
        } else if (id == R.id.menu_image_shape) {
            showImageShapeDialog();
            return true;
        } else if (id == R.id.menu_async_thread) {
            asyncThreadHandler.handleAsyncThread();
            return true;
        } else if (id == R.id.menu_sync_method) {
            syncMethodHandler.handleSyncMethod();
            return true;
        } else if (id == R.id.menu_rectangle_shape) {
            imageShapeHandler.setRectangleShape();
            return true;
        } else if (id == R.id.menu_circle_shape) {
            imageShapeHandler.setCircleShape();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    public void setUseAsyncTask(boolean useAsyncTask) {
        this.useAsyncTask = useAsyncTask;  // Изменяем флаг для выбора метода
    }

    private void showCommunicationMethodDialog() {
        String[] methods = {"Thread", "AsyncTask"};
        int selectedIndex = useAsyncTask ? 1 : 0;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("통신 방법 선택")
                .setSingleChoiceItems(methods, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        useAsyncTask = (which == 1);
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this,
                                "통신 방법: " + methods[which], Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
    public String downloadJson(String urlString) {
        String jsonString = "";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Создаем URL из строки
            URL url = new URL(urlString);

            // Открываем соединение
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Читаем ответ
            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder buffer = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            // Преобразуем собранный текст в строку
            jsonString = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jsonString;
    }

   public void loadDataWithThread() {
    progressBar.setVisibility(View.VISIBLE);  // Показываем прогресс

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                // Загружаем данные из сети
                String jsonString = downloadJson(JSON_URL);

                // Парсим данные
                List<Employee> employees = new SyncMethodHandler(MainActivity.this).parseJsonToEmployees(jsonString);

                // Сохраняем данные в базе данных
                dbHelper.clearAllEmployees();
                for (Employee employee : employees) {
                    dbHelper.addEmployee(employee);
                }

                // Обновляем UI на главном потоке
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        loadEmployeesFromDatabase();  // Загружаем сотрудников из базы данных
                        Toast.makeText(MainActivity.this, "데이터 로드 완료", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "데이터 로드 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    });
    thread.start();  // Запускаем поток
}

    private void showImageShapeDialog() {
        String[] shapes = {"사각형", "원형"};
        int selectedIndex = useCircularImage ? 1 : 0;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이미지 모양 선택")
                .setSingleChoiceItems(shapes, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        useCircularImage = (which == 1);
                        adapter.setCircularImage(useCircularImage);
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this,
                                "이미지 모양: " + shapes[which], Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
    private void toggleBackgroundMusic() {
        if (backgroundMusicEnabled) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            backgroundMusicEnabled = false;
            Toast.makeText(this, "배경음악 끔", Toast.LENGTH_SHORT).show();
        } else {
            if (mediaPlayer == null) {
                initBackgroundMusic();
            } else {
                mediaPlayer.start();
            }
            backgroundMusicEnabled = true;
            Toast.makeText(this, "배경음악 켬", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
