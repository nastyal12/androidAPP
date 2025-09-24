package com.example.afinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "employee.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_EMPLOYEES = "employees";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_SALARY = "salary";
    private static final String COLUMN_IMAGE = "image";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EMPLOYEES_TABLE = "CREATE TABLE " + TABLE_EMPLOYEES + "("
                + COLUMN_ID + " TEXT PRIMARY KEY,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_GENDER + " TEXT,"
                + COLUMN_SALARY + " INTEGER,"
                + COLUMN_IMAGE + " TEXT" + ")";
        db.execSQL(CREATE_EMPLOYEES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEES);
        onCreate(db);
    }

    // Add employee
    public void addEmployee(Employee employee) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, employee.getId());
        values.put(COLUMN_NAME, employee.getName());
        values.put(COLUMN_GENDER, employee.getGender());
        values.put(COLUMN_SALARY, employee.getSalary());
        values.put(COLUMN_IMAGE, employee.getImage());

        db.insert(TABLE_EMPLOYEES, null, values);
        db.close();
    }

    // Get all employees
    public List<Employee> getAllEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EMPLOYEES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Employee employee = new Employee();
                employee.setId(cursor.getString(0));
                employee.setName(cursor.getString(1));
                employee.setGender(cursor.getString(2));
                employee.setSalary(cursor.getInt(3));
                employee.setImage(cursor.getString(4));

                employeeList.add(employee);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return employeeList;
    }

    // Get employees by gender
    public List<Employee> getEmployeesByGender(String gender) {
        List<Employee> employeeList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EMPLOYEES + " WHERE " + COLUMN_GENDER + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{gender});

        if (cursor.moveToFirst()) {
            do {
                Employee employee = new Employee();
                employee.setId(cursor.getString(0));
                employee.setName(cursor.getString(1));
                employee.setGender(cursor.getString(2));
                employee.setSalary(cursor.getInt(3));
                employee.setImage(cursor.getString(4));

                employeeList.add(employee);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return employeeList;
    }

    // Get employees sorted by salary
    public List<Employee> getEmployeesSortedBySalary(boolean ascending) {
        List<Employee> employeeList = new ArrayList<>();
        String orderBy = ascending ? "ASC" : "DESC";
        String selectQuery = "SELECT * FROM " + TABLE_EMPLOYEES + " ORDER BY " + COLUMN_SALARY + " " + orderBy;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Employee employee = new Employee();
                employee.setId(cursor.getString(0));
                employee.setName(cursor.getString(1));
                employee.setGender(cursor.getString(2));
                employee.setSalary(cursor.getInt(3));
                employee.setImage(cursor.getString(4));

                employeeList.add(employee);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return employeeList;
    }

    // Get employees by gender sorted by salary
    public List<Employee> getEmployeesByGenderSortedBySalary(String gender, boolean ascending) {
        List<Employee> employeeList = new ArrayList<>();
        String orderBy = ascending ? "ASC" : "DESC";
        String selectQuery = "SELECT * FROM " + TABLE_EMPLOYEES +
                " WHERE " + COLUMN_GENDER + " = ? ORDER BY " + COLUMN_SALARY + " " + orderBy;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{gender});

        if (cursor.moveToFirst()) {
            do {
                Employee employee = new Employee();
                employee.setId(cursor.getString(0));
                employee.setName(cursor.getString(1));
                employee.setGender(cursor.getString(2));
                employee.setSalary(cursor.getInt(3));
                employee.setImage(cursor.getString(4));

                employeeList.add(employee);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return employeeList;
    }

    // Clear all employees
    public void clearAllEmployees() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EMPLOYEES, null, null);
        db.close();
    }

    // Check if table is empty
    public boolean isEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_EMPLOYEES, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count == 0;
    }
}
