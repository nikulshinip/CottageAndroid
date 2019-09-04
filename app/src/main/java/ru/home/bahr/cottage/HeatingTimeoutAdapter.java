package ru.home.bahr.cottage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HeatingTimeoutAdapter extends BaseAdapter {
    private static final String IN = "inTimeOut";
    private static final String OUT = "outTimeOut";
    private static final String COMMENT = "comment";
    private static final String LINEID = "line";


    private Activity context;                           //Activity
    private ArrayList<Map<String, String>> arrayMap;    //данные наложения
    private static String autorization;
    private static String urlDelete;
    private static String urlEdit;

    private View dialogCustumTitle;
    private TextView titleText;
    private View dialogCustumMessage;
    private TextView messageText;
    private TextView messageLine;
    private EditText editFromEditText;
    private EditText editToEditText;
    private EditText editCommentEditText;

    public HeatingTimeoutAdapter(Activity context, ArrayList<Map<String, String>> arrayMap, String autorization, String url){
        this.context = context;
        this.arrayMap = arrayMap;
        this.autorization = autorization;
        urlDelete = "http://" + url + "/boiler/delete/";
        urlEdit = "http://" + url + "/boiler/edittimeout/";
    }

    static class HolderView{
        public TextView fromText;
        public TextView toText;
        public TextView commentText;
        public ImageButton editButtom;
        public ImageButton deleteButtom;
    }

    @Override
    public int getCount() {
        return arrayMap.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayMap.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        HolderView holder = new HolderView();
        View rowView = view;

        if(rowView==null){
//            rowView = layoutView;
            LayoutInflater inflater = context.getLayoutInflater();                  //создаем inflater
            rowView = inflater.inflate(R.layout.heating_timeout_item, null, true);             //и заполняем из xml

            holder.fromText = (TextView) rowView.findViewById(R.id.timeOutInText);
            holder.toText = (TextView) rowView.findViewById(R.id.timeOutOutText);
            holder.commentText = (TextView) rowView.findViewById(R.id.timeOutCommentText);
            holder.editButtom = (ImageButton) rowView.findViewById(R.id.editButtom);
            holder.deleteButtom = (ImageButton) rowView.findViewById(R.id.deleteButtom);
            rowView.setTag(holder);
        }else{
            holder = (HolderView) rowView.getTag();                                     //если строка создана то передаем ее в holder
        }

        holder.fromText.setText(arrayMap.get(i).get(IN));
        holder.toText.setText(arrayMap.get(i).get(OUT));
        holder.commentText.setText(arrayMap.get(i).get(COMMENT));
        holder.editButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

                dialogCustumTitle = (LinearLayout) context.getLayoutInflater().inflate(R.layout.dialog_title, null);
                dialogBuilder.setCustomTitle(dialogCustumTitle);
                titleText = (TextView) dialogCustumTitle.findViewById(R.id.dialogTitleText);
                titleText.setText(R.string.editTitle);

                dialogCustumMessage = (LinearLayout) context.getLayoutInflater().inflate(R.layout.dialog_edit_message, null);
                dialogBuilder.setView(dialogCustumMessage);
                titleText = (TextView) dialogCustumMessage.findViewById(R.id.dialogMessageText);
                titleText.setText(R.string.editMessage);
                editFromEditText = (EditText) dialogCustumMessage.findViewById(R.id.fromEditText);
                editFromEditText.setText(arrayMap.get(i).get(IN));
                editToEditText = (EditText) dialogCustumMessage.findViewById(R.id.toEditText);
                editToEditText.setText(arrayMap.get(i).get(OUT));
                editCommentEditText = (EditText) dialogCustumMessage.findViewById(R.id.commentEditText);
                editCommentEditText.setText(arrayMap.get(i).get(COMMENT));

                dialogBuilder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int r) {
                        Map<String, String> edit = new HashMap<String, String>();
                        edit.put("id", arrayMap.get(i).get(LINEID));
                        edit.put("from", String.valueOf(editFromEditText.getText()));
                        edit.put("before", String.valueOf(editToEditText.getText()));
                        edit.put("title", String.valueOf(editCommentEditText.getText()));

                        EditItem editItem = new EditItem(edit);
                        editItem.execute(urlEdit, autorization);
                    }
                });

                dialogBuilder.setNegativeButton(R.string.cansel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int r) {

                    }
                });

                AlertDialog editDialog = dialogBuilder.create();
                editDialog.show();
            }
        });
        holder.deleteButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

                dialogCustumTitle = (LinearLayout) context.getLayoutInflater().inflate(R.layout.dialog_title, null);
                dialogBuilder.setCustomTitle(dialogCustumTitle);
                titleText = (TextView) dialogCustumTitle.findViewById(R.id.dialogTitleText);
                titleText.setText(R.string.deleteTitle);

                dialogCustumMessage = (LinearLayout) context.getLayoutInflater().inflate(R.layout.dialog_message, null);
                dialogBuilder.setView(dialogCustumMessage);
                messageText = (TextView) dialogCustumMessage.findViewById(R.id.dialogMessageText);
                messageText.setText(R.string.deleteMessage);
                messageLine = (TextView) dialogCustumMessage.findViewById(R.id.dialogMessageTextString);
                messageLine.setText(arrayMap.get(i).get(IN) + " - " + arrayMap.get(i).get(OUT) + "  " + arrayMap.get(i).get(COMMENT));

                dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int r) {
                        delete(arrayMap.get(i).get(LINEID));
                    }
                });

                dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int r) {

                    }
                });

                AlertDialog deleteDialog = dialogBuilder.create();
                deleteDialog.show();
            }
        });

        return rowView;
    }

    private void delete(String line){
        Map<String, String> delId = new HashMap<String, String>();
        delId.put("id", line);

        DeleteItem deleteItem = new DeleteItem(delId);
        deleteItem.execute(urlDelete, autorization);
    }

    private class DeleteItem extends AsyncTask<String, Void, Boolean>{
        private ServerConnectUpload connect;
        private Map<String, String> delete;

        public DeleteItem(Map<String, String> delete){
            this.delete = delete;
        }

        @Override
        protected Boolean doInBackground(String... param){
            connect = new ServerConnectUpload(param[0], param[1], delete);
            return connect.getUrl();
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                Toast.makeText(context, context.getResources().getString(R.string.deleteYes), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, HeatingTimeout.class);
                context.startActivity(intent);
                return;
            }else{
                Toast.makeText(context, context.getResources().getString(R.string.deleteNo), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class EditItem extends AsyncTask<String, Void, Boolean>{
        private ServerConnectUpload connect;
        private Map<String, String> edit;

        public EditItem(Map<String, String> edit){
            this.edit = edit;
        }

        @Override
        protected Boolean doInBackground(String... param){
            connect = new ServerConnectUpload(param[0], param[1], edit);
            return connect.getUrl();
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                Toast.makeText(context, context.getResources().getString(R.string.editTrue), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, HeatingTimeout.class);
                context.startActivity(intent);
            }else{
                Toast.makeText(context, context.getResources().getString(R.string.editError), Toast.LENGTH_LONG).show();
            }
        }
    }

}
