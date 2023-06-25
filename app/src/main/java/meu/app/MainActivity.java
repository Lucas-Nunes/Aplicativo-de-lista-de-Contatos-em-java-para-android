package meu.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.view.View.OnClickListener;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InicialComponentes();
    }

    private void InicialComponentes()
    {
        ExibirContatos();
    }

    private void ExibirContatos()
    {
        LinearLayout panel03 = findViewById(R.id.panel03);
        LinearLayout panel01 = findViewById(R.id.panel01);
        FrameLayout panel02 = findViewById(R.id.panel02);
        panel01.removeAllViews();
        panel02.removeAllViews();
        panel03.removeAllViews();

        AdicionarContatosButton();

        DatabaseHelper dbHelper;
        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db = dbHelper.getWritableDatabase();

        String[] colunas = {"nome", "numero"};
        SQLiteDatabase db2 = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db2.query("contatos", colunas, null, null, null, null, null);
            int indiceNome = cursor.getColumnIndex("nome");
            int indiceNumero = cursor.getColumnIndex("numero");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String nome = cursor.getString(indiceNome);
                    String numero = cursor.getString(indiceNumero);
                    Button button = new Button(this);
                    button.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    button.setText("Nome: " + nome + "\n" + "Numero: " + numero);
                    button.setGravity(Gravity.LEFT);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v){
                            ExcluirContatos(nome);
                        }
                    });
                    panel01.addView(button);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();

            TextView errorTextView = panel01.findViewById(R.id.errorTextView);
            errorTextView.setText("Ocorreu um erro: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db2.close();
        }
    }

    private void AdicionarContatosButton()
    {
        FrameLayout panel02 = findViewById(R.id.panel02);
        try
        {
            Button button = new Button(this);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) button.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
            }
            layoutParams.leftMargin = 500;
            layoutParams.topMargin = 1100;

            button.setLayoutParams(layoutParams);
            button.setText("Adicionar Contato");
            button.setGravity(Gravity.CENTER);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    AdicionarContatos();
                }
            });
            panel02.addView(button);
        }
        catch (Exception e) {
            e.printStackTrace();

            TextView errorTextView = panel02.findViewById(R.id.errorTextView02);
            errorTextView.setText("Ocorreu um erro: " + e.getMessage());
        }
    }
    private void AdicionarContatos()
    {
        LinearLayout panel01 = findViewById(R.id.panel01);
        FrameLayout panel02 = findViewById(R.id.panel02);
        panel01.removeAllViews();
        panel02.removeAllViews();

        LinearLayout panel03 = findViewById(R.id.panel03);
        EditText editNome = new EditText(this);
        editNome.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        editNome.setHint("Nome completo:");

        panel03.addView(editNome);

        EditText editNumero = new EditText(this);
        editNumero.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        editNumero.setHint("Numero");

        panel03.addView(editNumero);

        Button button = new Button(this);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) button.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
        }
        layoutParams.leftMargin = 0;
        layoutParams.topMargin = 70;

        button.setLayoutParams(layoutParams);
        button.setText("Salvar");
        button.setGravity(Gravity.CENTER);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String nome = editNome.getText().toString();
                String numero = editNumero.getText().toString();
                AdicionarContatos2(nome, numero);
            }
        });
        panel03.addView(button);
    }

    private void ExcluirContatos(String nome)
    {
        DatabaseHelper dbHelper;
        dbHelper = new DatabaseHelper(this);

        new AlertDialog.Builder(this)
                .setTitle("Confirmação")
                .setMessage("Deseja realmente excluir o contato " + nome + "?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        LinearLayout panel01 = findViewById(R.id.panel01);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db = dbHelper.getWritableDatabase();
                        String tabela = "contatos";
                        String whereClause = "nome=?";
                        String[] whereArgs = { nome };
                        db.delete(tabela, whereClause, whereArgs);
                        db.close();

                        int childCount = panel01.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            View controles = panel01.getChildAt(i);
                            if (controles instanceof Button) {
                                Button button = (Button) controles;
                                String buttonText = button.getText().toString();
                                if (buttonText.contains(nome)) {
                                    panel01.removeView(button);
                                    break;
                                }
                            }
                        }
                        ExibirContatos();
                        Toast.makeText(MainActivity.this, "Contato excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void AdicionarContatos2(String nome, String numero)
    {
        DatabaseHelper dbHelper;
        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db = dbHelper.getWritableDatabase();
        LinearLayout panel01 = findViewById(R.id.panel01);
        try {
            ContentValues values = new ContentValues();
            values.put("nome", nome);
            values.put("numero", numero);
            long id = db.insert("contatos", null, values);
            ExibirContatos();
        } catch (Exception e) {
            e.printStackTrace();
            TextView errorTextView = panel01.findViewById(R.id.errorTextView);
            errorTextView.setText("Ocorreu um erro: " + e.getMessage());
        } finally {
            if (db != null) {db.close();}
        }
    }
}
