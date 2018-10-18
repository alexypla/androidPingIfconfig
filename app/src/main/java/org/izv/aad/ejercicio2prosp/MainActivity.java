package org.izv.aad.ejercicio2prosp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    Runtime rt = Runtime.getRuntime();
    Process proceso;
    private TextView tv1;
    protected TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt1 = (Button) findViewById(R.id.button);
        Button bt2 = (Button) findViewById(R.id.button2);
        Button bt3 = (Button) findViewById(R.id.button3);
        TextView tv1 = (TextView) findViewById(R.id.textView);
        TextView tv2 = (TextView) findViewById(R.id.textView2);

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (proceso != null) {
                    System.out.println(System.currentTimeMillis());
                    System.out.println(System.nanoTime());
                    proceso.destroy();
                    try {
                        proceso.waitFor();
                    } catch (InterruptedException ex) {
                        System.out.println(ex.toString());
                    }
                }
            }
        });

    }
            public void onClick(View v) {
                try {
                    String cadena = "ifconfig";
                    proceso = rt.exec(cadena);
                    InputStream is = proceso.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);//clase filtro
                    final BufferedReader br = new BufferedReader(isr);
                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String linea = "";
                            String lineaAnterior="";
                            String salida="";
                            try {
                                while ((linea = br.readLine()) != null) {
                                    System.out.println(linea);
                                    if (linea.contains("inet addr:")){
                                        linea=linea.substring(linea.indexOf("inet addr:")+10,linea.length());
                                        linea=linea.substring(0,linea.indexOf(" "));
                                        lineaAnterior=lineaAnterior.substring(0,lineaAnterior.indexOf("Link"));
                                        lineaAnterior=lineaAnterior.trim();
                                        salida+=lineaAnterior+":"+linea+"\n";
                                    }
                                    lineaAnterior=linea;
                                }
                                MainActivity.this.tvSetText(tv1, salida);

                            } catch (IOException ex) {
                                System.out.println(ex.toString());
                            }
                        }
                    });
                    th.start();
                } catch (IOException ex) {
                    System.out.println(ex.toString());
                }



    }
            public void bt3(View v) {
                try {

                    String cadena="ping -c 1 8.8.8.8";
                    proceso = rt.exec(cadena);
                    InputStream is = proceso.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);//clase filtro
                    final BufferedReader br = new BufferedReader(isr);
                    Thread th=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String linea="";
                            try {
                                boolean conectado = false;
                                while ((linea = br.readLine()) != null) {
                                    if (linea.contains("packets transmitted") && !linea.contains(", 0 received")) {
                                        conectado = true;
                                        break;}
                                    System.out.println(linea);


                                }
                                MainActivity.this.tvSetText(tv2, conectado ? "Con conexion" : "Sin conexion");
                            } catch (IOException ex) {
                                System.out.println(ex.toString());
                           }
                        }
                    });
                    th.start();
                } catch (IOException ex) {
                    System.out.println(ex.toString());                }

            }


    private void tvSetText(final TextView tv, final String datos) {
        tv.post(new Runnable() {
            @Override
            public void run() {
                tv.setText(datos);
            }
        });
    }
    }



