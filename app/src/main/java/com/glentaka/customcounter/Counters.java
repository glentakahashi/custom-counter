package com.glentaka.customcounter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Counters extends Activity {

    CounterAdapter adapter;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
    private class AddCounterDialogOnClickListener implements View.OnClickListener {

        private AlertDialog diag;

        public AddCounterDialogOnClickListener(AlertDialog diag) {
            this.diag = diag;
        }

        @Override
        public void onClick(View view) {
            String text = ((EditText) diag.findViewById(R.id.counterNameEntry)).getText().toString();
            System.out.println(text);
            if (text.length() == 0) {
                Toast.makeText(diag.getContext(), "Please enter a name.", Toast.LENGTH_LONG).show();
                return;
            }
            adapter.addCounter(text);
            diag.dismiss();
        }
    }

    private class EditCounterDialogOnClickListener implements View.OnClickListener {

        private AlertDialog diag;
        private Counter c;

        public EditCounterDialogOnClickListener(AlertDialog diag, Counter c) {
            this.diag = diag;
            this.c = c;
        }

        @Override
        public void onClick(View view) {
            String text = ((EditText) diag.findViewById(R.id.counterNameEdit)).getText().toString();
            System.out.println(text);
            if (text.length() == 0) {
                Toast.makeText(diag.getContext(), "Please enter a name.", Toast.LENGTH_LONG).show();
                return;
            }
            c.setName(text);
            adapter.notifyDataSetChanged();
            diag.dismiss();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counters);
        GridView g = (GridView) findViewById(R.id.counterGridView);
        adapter = new CounterAdapter(g.getContext());
        g.setAdapter(adapter);
        g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Counter c = (Counter) adapter.getItem(i);
                c.click();
                ((Button) view).setText(c.getText());
            }
        });
        g.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Counters.this);
                LayoutInflater inflater = Counters.this.getLayoutInflater();
                builder.setMessage(R.string.edit_counter_dialog)
                        .setView(inflater.inflate(R.layout.dialog_editcounter, null))
                        .setPositiveButton(R.string.edit_counter_dialog_pos, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //do nothing
                            }
                        })
                        .setNegativeButton(R.string.edit_counter_dialog_neg, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // will close automatically
                            }
                        });
                final Counter c = (Counter) adapter.getItem(i);
                // Create the AlertDialog object and return it
                final AlertDialog diag = builder.create();
                diag.show();
                ((EditText) diag.findViewById(R.id.counterNameEdit)).setText(c.getName());
                ((Button) diag.findViewById(R.id.resetCounterButton)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        c.reset();
                        adapter.notifyDataSetChanged();
                    }
                });
                ((Button) diag.findViewById(R.id.removeCounterButton)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.removeItem(i);
                        diag.dismiss();
                    }
                });
                diag.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new EditCounterDialogOnClickListener(diag, c));
                return true;
            }
        });
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Counters.this);
                LayoutInflater inflater = Counters.this.getLayoutInflater();
                builder.setMessage(R.string.add_counter_dialog)
                        .setView(inflater.inflate(R.layout.dialog_addcounter, null))
                        .setPositiveButton(R.string.add_counter_dialog_pos, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //do nothing
                            }
                        })
                        .setNegativeButton(R.string.add_counter_dialog_neg, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            // will close automatically
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog diag = builder.create();
                diag.show();
                diag.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new AddCounterDialogOnClickListener(diag));
            }
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.reset_confirmation);
        builder.setTitle("Reset");
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapter.reset();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        Button exportButton = (Button) findViewById(R.id.exportButton);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                File folder = Environment.getExternalStorageDirectory();
                if(!folder.exists()) {
                    folder.mkdirs();
                }
                File csv = new File(folder, "/customcounter-export-"+System.currentTimeMillis()+".csv");
                try {
                    FileOutputStream fos = new FileOutputStream(csv);
                    fos.write("Counter,Click\n".getBytes());
                    for (Counter counter : adapter.getCounters()) {
                        for (Date d : counter.getClicks()) {
                            fos.write((counter.getName() + "," + DATE_FORMAT.format(d) +"\n").getBytes());
                        }
                    }
                    fos.close();
                    Uri uri = Uri.fromFile(csv);
                    i.putExtra(Intent.EXTRA_SUBJECT, "CustomCounter Export " + new Date());
                    i.putExtra(Intent.EXTRA_STREAM, uri);
                    i.setType("text/csv");
                    Counters.this.startActivity(Intent.createChooser(i, "Export as .csv"));
                } catch (IOException e) {
                    Toast.makeText(Counters.this , getString(R.string.export_failed), Toast.LENGTH_LONG).show();
                }
            }
        });
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File folder = Environment.getExternalStorageDirectory();
                if(!folder.exists()) {
                    folder.mkdirs();
                }
                File csv = new File(folder, "/customcounter-export-"+System.currentTimeMillis()+".csv");
                try {
                    FileOutputStream fos = new FileOutputStream(csv);
                    fos.write("Counter,Click\n".getBytes());
                    for (Counter counter : adapter.getCounters()) {
                        for (Date d : counter.getClicks()) {
                            fos.write((counter.getName() + "," + DATE_FORMAT.format(d) +"\n").getBytes());
                        }
                    }
                    fos.close();
                    Toast.makeText(Counters.this , "Saved as " + csv, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(Counters.this , getString(R.string.save_failed), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.counters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
