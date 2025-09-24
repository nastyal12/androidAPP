package com.example.afinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class EmployeeAdapter extends BaseAdapter {

    private Context context;
    private List<Employee> employees;
    private LayoutInflater inflater;
    private boolean isCircularImage = false; // 추가 옵션용

    public EmployeeAdapter(Context context, List<Employee> employees) {
        this.context = context;
        this.employees = employees;
        this.inflater = LayoutInflater.from(context);
    }

    public void setCircularImage(boolean circularImage) {
        this.isCircularImage = circularImage;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return employees.size();
    }

    @Override
    public Object getItem(int position) {
        return employees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.employee_item, parent, false);
            holder = new ViewHolder();
            holder.imgEmployee = convertView.findViewById(R.id.img_employee);
            holder.txtName = convertView.findViewById(R.id.txt_name);
            holder.txtId = convertView.findViewById(R.id.txt_id);
            holder.txtGender = convertView.findViewById(R.id.txt_gender);
            holder.txtSalary = convertView.findViewById(R.id.txt_salary);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Employee employee = employees.get(position);

        // Set text data
        holder.txtName.setText(employee.getName());
        holder.txtId.setText("사번: " + employee.getId());
        holder.txtGender.setText("성별: " + employee.getGender());

        // Format salary with comma
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
        holder.txtSalary.setText("급여: " + formatter.format(employee.getSalary()) + "원");

        // Load image using Glide
        RequestOptions requestOptions = new RequestOptions();

        if (isCircularImage) {
            requestOptions = requestOptions.circleCrop();
        }

        Glide.with(context)
                .load(employee.getImage())
                .apply(requestOptions)
                .into(holder.imgEmployee);

        return convertView;
    }

    // Update the employee list
    public void updateEmployees(List<Employee> newEmployees) {
        this.employees = newEmployees;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView imgEmployee;
        TextView txtName;
        TextView txtId;
        TextView txtGender;
        TextView txtSalary;
    }
}
