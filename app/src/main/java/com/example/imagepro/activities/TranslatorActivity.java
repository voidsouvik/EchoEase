package com.example.imagepro.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagepro.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings("unchecked")
public class TranslatorActivity extends AppCompatActivity {

    private Spinner fromSpinner, toSpinner;
    private EditText sourceText;
    private ImageView micIV;
    private Button translateBtn;
    private TextView translateTV;

    String[] fromLanguage = {"From", "English", "Afrikaans", "Irish", "Albanian", "Italian", "Arabic", "Japanese",
            "Kannada", "Korean", "Bengali", "Korean", "Belarusian", "Latvian", "Bulgarian", "Lithuanian", "Catalan", "Macedonian", "Chinese Simplified", "Malay"
            , "Chinese Traditional", "Maltese", "Croatian", "Norwegian", "Czech", "Persian", "Danish", "Polish", "Dutch", "Portuguese", "Romanian", "Esperanto", "Russian", "Estonian", "Filipino"
            , "Slovak", "Finnish", "Slovenian", "French", "Spanish", "Galician", "Swahili", "Georgian", "Swedish", "German", "Tamil", "Greek", "Telugu", "Gujarati", "Haitian Creole", "Turkish"
            , "Ukrainian", "Hindi", "Urdu", "Hungarian", "Vietnamese", "Icelandic", "Welsh", "Indonesian"};

    String[] toLanguage = {"From", "English", "Afrikaans", "Irish", "Albanian", "Italian", "Arabic", "Japanese",
            "Kannada", "Korean", "Bengali", "Korean", "Belarusian", "Latvian", "Bulgarian", "Lithuanian", "Catalan", "Macedonian", "Chinese Simplified", "Malay"
            , "Chinese Traditional", "Maltese", "Croatian", "Norwegian", "Czech", "Persian", "Danish", "Polish", "Dutch", "Portuguese", "Romanian", "Esperanto", "Russian", "Estonian", "Filipino"
            , "Slovak", "Finnish", "Slovenian", "French", "Spanish", "Galician", "Swahili", "Georgian", "Swedish", "German", "Tamil", "Greek", "Telugu", "Gujarati", "Haitian Creole", "Turkish"
            , "Ukrainian", "Hindi", "Urdu", "Hungarian", "Vietnamese", "Icelandic", "Welsh", "Indonesian"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    int languageCode, fromLanguageCode, toLanguageCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);

        fromSpinner = findViewById(R.id.idFromSpinner);
        toSpinner = findViewById(R.id.idToSpinner);
        sourceText = findViewById(R.id.idEditSource);
        micIV = findViewById(R.id.idIVMic);
        translateBtn = findViewById(R.id.idBtnTranslation);
        translateTV = findViewById(R.id.idTranslatedTV);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLanguageCode = getLanguageCode(fromLanguage[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.spinner_item, fromLanguage);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLanguageCode = getLanguageCode(toLanguage[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.spinner_item, toLanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something to translate");
                try {
                    startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(TranslatorActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateTV.setVisibility(View.VISIBLE);
                translateTV.setText("");
                if (sourceText.getText().toString().isEmpty()){
                    Toast.makeText(TranslatorActivity.this, "Please enter text to translate", Toast.LENGTH_SHORT).show();
                }else if (fromLanguageCode == 0){
                    Toast.makeText(TranslatorActivity.this, "Please select Source Language", Toast.LENGTH_SHORT).show();
                }else if (toLanguageCode == 0){
                    Toast.makeText(TranslatorActivity.this, "Please select the language to make translation", Toast.LENGTH_SHORT).show();
                }else {
                    translateText(fromLanguageCode, toLanguageCode, sourceText.getText().toString());
                }
            }
        });

    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source) {

        translateTV.setText("Downloading model, please wait...");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translateTV.setText("Translation..");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translateTV.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TranslatorActivity.this, "Failed to translate!! try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TranslatorActivity.this, "Failed to download model!! Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            sourceText.setText(result.get(0));
        }
    }

    // String[] fromLanguage = {"From", "English", "Afrikaans", "Arabic", "Belarusian", "Bulgarian", "Bengali", "Catalan", "Czech",
    //            "Welsh", "Hindi", "Urdu"};
    private int getLanguageCode(String language) {
        int languageCode = 0;
        switch (language){
            case "English":
                languageCode = FirebaseTranslateLanguage.EN;
                break;
            case "Afrikaans":
                languageCode = FirebaseTranslateLanguage.AF;
                break;
            case "Irish":
                languageCode = FirebaseTranslateLanguage.GA;
                break;
            case "Albanian":
                languageCode = FirebaseTranslateLanguage.SQ;
                break;
            case "Italian":
                languageCode = FirebaseTranslateLanguage.IT;
                break;
            case "Arabic":
                languageCode = FirebaseTranslateLanguage.AR;
                break;
            case "Japanese":
                languageCode = FirebaseTranslateLanguage.JA;
                break;
            case "Kannada":
                languageCode = FirebaseTranslateLanguage.KN;
                break;
            case "Korean":
                languageCode = FirebaseTranslateLanguage.KO;
                break;
            case "Bengali":
                languageCode = FirebaseTranslateLanguage.BN;
                break;
            case "Belarusian":
                languageCode = FirebaseTranslateLanguage.BE;
                break;
            case "Latvian":
                languageCode = FirebaseTranslateLanguage.LV;
                break;
            case "Bulgarian":
                languageCode = FirebaseTranslateLanguage.BG;
                break;
            case "Lithuanian":
                languageCode = FirebaseTranslateLanguage.LT;
                break;
            case "Catalan":
                languageCode = FirebaseTranslateLanguage.CA;
                break;
            case "Macedonian":
                languageCode = FirebaseTranslateLanguage.MK;
                break;
            case "Chinese Simplified":
                languageCode = FirebaseTranslateLanguage.ZH;
                break;
            case "Malay":
                languageCode = FirebaseTranslateLanguage.MS;
                break;
            case "Chinese Traditional":
                languageCode = FirebaseTranslateLanguage.ZH;
                break;
            case "Maltese":
                languageCode = FirebaseTranslateLanguage.MT;
                break;
            case "Croatian":
                languageCode = FirebaseTranslateLanguage.HR;
                break;
            case "Norwegian":
                languageCode = FirebaseTranslateLanguage.NO;
                break;
            case "Czech":
                languageCode = FirebaseTranslateLanguage.CS;
                break;
            case "Persian":
                languageCode = FirebaseTranslateLanguage.FA;
                break;
            case "Danish":
                languageCode = FirebaseTranslateLanguage.DA;
                break;
            case "Polish":
                languageCode = FirebaseTranslateLanguage.PL;
                break;
            case "Dutch":
                languageCode = FirebaseTranslateLanguage.NL;
                break;
            case "Portuguese":
                languageCode = FirebaseTranslateLanguage.PT;
                break;
            case "Romanian":
                languageCode = FirebaseTranslateLanguage.RO;
                break;
            case "Esperanto":
                languageCode = FirebaseTranslateLanguage.EO;
                break;
            case "Russian":
                languageCode = FirebaseTranslateLanguage.RU;
                break;
            case "Estonian":
                languageCode = FirebaseTranslateLanguage.ET;
                break;
            case "Filipino":
                languageCode = FirebaseTranslateLanguage.TL;
                break;
            case "Slovak":
                languageCode = FirebaseTranslateLanguage.SK;
                break;
            case "Finnish":
                languageCode = FirebaseTranslateLanguage.FI;
                break;
            case "Slovenian":
                languageCode = FirebaseTranslateLanguage.SL;
                break;
            case "French":
                languageCode = FirebaseTranslateLanguage.FR;
                break;
            case "Spanish":
                languageCode = FirebaseTranslateLanguage.ES;
                break;
            case "Galician":
                languageCode = FirebaseTranslateLanguage.GL;
                break;
            case "Swahili":
                languageCode = FirebaseTranslateLanguage.SW;
                break;
            case "Georgian":
                languageCode = FirebaseTranslateLanguage.KA;
                break;
            case "Swedish":
                languageCode = FirebaseTranslateLanguage.SV;
                break;
            case "German":
                languageCode = FirebaseTranslateLanguage.DE;
                break;
            case "Tamil":
                languageCode = FirebaseTranslateLanguage.TA;
                break;
            case "Greek":
                languageCode = FirebaseTranslateLanguage.EL;
                break;
            case "Telugu":
                languageCode = FirebaseTranslateLanguage.TE;
                break;
            case "Gujarati":
                languageCode = FirebaseTranslateLanguage.GU;
                break;
            case "Thai":
                languageCode = FirebaseTranslateLanguage.TH;
                break;
            case "Haitian Creole":
                languageCode = FirebaseTranslateLanguage.HT;
                break;
            case "Turkish":
                languageCode = FirebaseTranslateLanguage.TR;
                break;
            case "Ukrainian":
                languageCode = FirebaseTranslateLanguage.UK;
                break;
            case "Hindi":
                languageCode = FirebaseTranslateLanguage.HI;
                break;
            case "Urdu":
                languageCode = FirebaseTranslateLanguage.UR;
                break;
            case "Hungarian":
                languageCode = FirebaseTranslateLanguage.HU;
                break;
            case "Vietnamese":
                languageCode = FirebaseTranslateLanguage.VI;
                break;
            case "Icelandic":
                languageCode = FirebaseTranslateLanguage.IS;
                break;
            case "Welsh":
                languageCode = FirebaseTranslateLanguage.CY;
                break;
            case "Indonesian":
                languageCode = FirebaseTranslateLanguage.ID;
                break;
            default:
                languageCode = 0;
        }
        return languageCode;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}