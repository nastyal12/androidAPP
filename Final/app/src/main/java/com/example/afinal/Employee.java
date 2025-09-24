package com.example.afinal;

public class Employee {
    private String id;
    private String name;
    private String gender;
    private int salary;
    private String image;

    public Employee() {
    }

    public Employee(String id, String name, String gender, int salary, String image) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.salary = salary;
        this.image = image;
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getSalary() {
        return salary;
    }

    public String getImage() {
        return image;
    }

    // Setter methods
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", salary=" + salary +
                ", image='" + image + '\'' +
                '}';
    }
}