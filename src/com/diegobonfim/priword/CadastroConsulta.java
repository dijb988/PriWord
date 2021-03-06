package com.diegobonfim.priword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class CadastroConsulta extends ActionBarActivity{
	
	ArrayList<Integer> classesSelecionadas = new ArrayList<Integer>();
	ArrayList<Integer> gruposSelecionados = new ArrayList<Integer>();
	ArrayList<String> classesSelecionadasAntes = null;
	ArrayList<String> classesExemplos = new ArrayList<String>();
	
	ArrayList<Palavras> palavras = new ArrayList<Palavras>();
	ArrayList<GruposPalavras> gruposPalavras = new ArrayList<GruposPalavras>();
	ArrayList<Grupos> grupos = new ArrayList<Grupos>();
	ArrayList<Frases> frases = new ArrayList<Frases>();
	
	MenuItem add, save, edit, cancel;
	
	SQLiteDatabase bancoDados = null;
	Cursor cursorPalavras, cursorGrupos, cursorGruposPalavras, cursorFrases;
	String nomeBanco = "vocabulario.db";
	String tabelaPalavras = "palavras"; 
	String[] camposTabelaPalavras = new String[]{"idPalava", "palavra", "definicao", "classes", "traducao", "irregular", "pastform", "pastparticiple"};
	int idPalava=-1;
	String tabelaGruposPalavras = "grupos_palavras";
	String[] camposTabelaGruposPalavras = new String[]{"grupoNome", "PalavraNome"};
	String tabelaGrupos = "grupos";
	String[] camposTabelaGrupos = new String[]{"idGrupos", "grupo"};
	String tabelaFrases = "frases";
	String[] camposTabelaFrases = new String[]{"idFrases", "frases", "palavraID"};
	
	EditText etNovoGrupo;
	
	EditText etCadastroConsultaClasses, etCadastroConsultaPalavras, etCadastroConsultaAddExemplo, 
				etCadastroConsultaDefinicao, etCadastroConsultaTraducao, etCadastroConsultaGrupo, etCadastroConsultaPastForm, 
					etCadastroConsultaPastParticiple;
	Button btCadastroConsultaAddExemplo;
	ListView lvCadastroConsultaExemplos;
	TextView tvCadastroConsultaFormaVerbos, tvCadastroConsultaPastForm, tvCadastroConsultaPastParticiple;
	CheckBox cbCadastroConsultaIrregular;

	boolean[] classesDefinidasBoolean = null;
	String palavraSelecionada ="";
	String[] classesExemplosString = null;
	String[] gruposCadastrados = null;
	String[] classesGramaticais = new String[] {"Adjective", "Adverb", "Article", "Conjunction", "Interjection", "Noun"
			, "Number",	"Phrasal Verb", "Postposition", "Preosition", "Pronoun", "Substantive",	"Verb"};
	
	boolean atualizando=false, erroPalavra=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cadastro_consulta);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abreouCriaBanco();
		
		etCadastroConsultaPalavras = (EditText) findViewById(R.id.cadastro_consulta_etPalavra);
		etCadastroConsultaPalavras.requestFocus();
		etCadastroConsultaDefinicao = (EditText) findViewById(R.id.cadastro_consulta_etDefinicao);
		etCadastroConsultaClasses = (EditText) findViewById(R.id.cadastro_consulta_etClasses);
		etCadastroConsultaPastForm = (EditText) findViewById(R.id.cadastro_consulta_etPastForm);
		etCadastroConsultaPastForm.setVisibility(android.view.View.GONE);
		etCadastroConsultaPastParticiple = (EditText) findViewById(R.id.cadastro_consulta_etPastParticiple);
		etCadastroConsultaPastParticiple.setVisibility(android.view.View.GONE);
		etCadastroConsultaGrupo = (EditText) findViewById(R.id.cadastro_consulta_etGrupos);
		etCadastroConsultaTraducao = (EditText) findViewById(R.id.cadastro_consulta_etTraducao);
		etCadastroConsultaAddExemplo = (EditText) findViewById(R.id.cadastro_consulta_etAddExemplo);
		etCadastroConsultaAddExemplo.setEnabled(false);
		btCadastroConsultaAddExemplo = (Button) findViewById(R.id.cadastro_consulta_btAddExemplo);
		btCadastroConsultaAddExemplo.setEnabled(false);
		cbCadastroConsultaIrregular = (CheckBox) findViewById(R.id.cadastro_consulta_cbIrregular);
		cbCadastroConsultaIrregular.setVisibility(android.view.View.GONE);
		tvCadastroConsultaFormaVerbos = (TextView) findViewById(R.id.cadastro_consulta_tvFormaVerbos);
		tvCadastroConsultaFormaVerbos.setVisibility(android.view.View.GONE);
		tvCadastroConsultaPastForm = (TextView) findViewById(R.id.cadastro_consulta_tvPastForm);
		tvCadastroConsultaPastForm.setVisibility(android.view.View.GONE);
		tvCadastroConsultaPastParticiple = (TextView) findViewById(R.id.cadastro_consulta_tvPastParticiple);
		tvCadastroConsultaPastParticiple.setVisibility(android.view.View.GONE);
		lvCadastroConsultaExemplos = (ListView) findViewById(R.id.cadastro_consulta_lvExemplos);
		
		Intent IDadosRecebidos = getIntent();
		if (IDadosRecebidos.getStringExtra("Palavra") != null){			
			palavraSelecionada = IDadosRecebidos.getStringExtra("Palavra");
			
			preencherTodaView(palavraSelecionada);
		}
				
		listener();
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        setContentView(R.layout.cadastro_consulta);

	    } else {
	        setContentView(R.layout.cadastro_consulta);
	    }
	}
	
	private void preencherTodaView(String palavra) {
		buscarDados();
		for (Palavras word : palavras){
			if (word.palavra.equals(palavra)){
				etCadastroConsultaPalavras.setText(palavra);
				idPalava = word.idPalava;
				etCadastroConsultaDefinicao.setText(word.definicao);
				etCadastroConsultaClasses.setText(word.classes);
				etCadastroConsultaTraducao.setText(word.traducao);
				if (word.classes.contains("Verb")){
					cbCadastroConsultaIrregular.setChecked(Boolean.valueOf(word.irregular));
					cbCadastroConsultaIrregular.setVisibility(android.view.View.VISIBLE);
					etCadastroConsultaPastForm.setText(word.pastform);
					etCadastroConsultaPastParticiple.setText(word.pastparticiple);
					etCadastroConsultaPastForm.setVisibility(android.view.View.VISIBLE);
					etCadastroConsultaPastParticiple.setVisibility(android.view.View.VISIBLE);
					tvCadastroConsultaFormaVerbos.setVisibility(android.view.View.VISIBLE);
					tvCadastroConsultaPastForm.setVisibility(android.view.View.VISIBLE);
					tvCadastroConsultaPastParticiple.setVisibility(android.view.View.VISIBLE);

				}
				String textoGrupo = "";
				for (GruposPalavras groupWord : gruposPalavras){
					if (groupWord.palavraNome.equals(palavra)){
						textoGrupo += groupWord.grupoNome +", ";
					}
				}
				if (textoGrupo.length()>1)
					textoGrupo = textoGrupo.substring(0, textoGrupo.length()-2);	
				etCadastroConsultaGrupo.setText(textoGrupo);
				classesExemplos.clear();
				for (Frases frasesToClasseExemplo : frases){
					if (frasesToClasseExemplo.palavrasID == idPalava){
						classesExemplos.add(frasesToClasseExemplo.frases);
					}
				}
				criarListaExemplo();
				break;
			}				
		}
		desabilitarCampos();
	}

	private void buscarDados() {
		try {
		   //Carrega dados da tabela tabelaPalavras
		   cursorPalavras = bancoDados.query(tabelaPalavras, camposTabelaPalavras, 
				   null,//selection, 
				   null,//selectionArgs, 
				   null,//groupBy, 
				   null,//having, 
				   null,//"order by palavra"//orderBy)
				   null); // Limite de registros retornados	
		   if (cursorPalavras.getCount() > 0){
			   cursorPalavras.moveToFirst();
			   palavras.clear();
			   do {
				   Palavras palavra = new Palavras();
				   palavra.idPalava = cursorPalavras.getInt(cursorPalavras.getColumnIndex("idPalava"));
				   palavra.palavra = cursorPalavras.getString(cursorPalavras.getColumnIndex("palavra"));
				   palavra.definicao = cursorPalavras.getString(cursorPalavras.getColumnIndex("definicao"));
				   palavra.classes = cursorPalavras.getString(cursorPalavras.getColumnIndex("classes"));
				   palavra.traducao = cursorPalavras.getString(cursorPalavras.getColumnIndex("traducao"));
				   palavra.irregular = cursorPalavras.getString(cursorPalavras.getColumnIndex("irregular"));
				   palavra.pastform = cursorPalavras.getString(cursorPalavras.getColumnIndex("pastform"));
				   palavra.pastparticiple = cursorPalavras.getString(cursorPalavras.getColumnIndex("pastparticiple"));
				   palavras.add(palavra);
			   } while (cursorPalavras.moveToNext());
		   }
		 //Carrega dados da tabela tabelaGrupos
		   cursorGrupos = bancoDados.query(tabelaGrupos, camposTabelaGrupos, 
				   null, null, null, null, null, null);
		   if (cursorGrupos.getCount() > 0){
			   cursorGrupos.moveToFirst();
			   grupos.clear();
			   do {
				   Grupos grupo = new Grupos();
				   grupo.idGrupos = cursorGrupos.getInt(cursorGrupos.getColumnIndex("idGrupos"));
				   grupo.grupo = cursorGrupos.getString(cursorGrupos.getColumnIndex("grupo"));
				   grupos.add(grupo);
			   } while (cursorGrupos.moveToNext());
		   }
		 //Carrega dados da tabela tabelaGruposPalavras
		   cursorGruposPalavras = bancoDados.query(tabelaGruposPalavras, camposTabelaGruposPalavras, 
				   null, null, null, null, null, null);
		   if (cursorGruposPalavras.getCount() > 0){
			   cursorGruposPalavras.moveToFirst();
			   gruposPalavras.clear();
			   do {
				   GruposPalavras grupoPalavra = new GruposPalavras();
				   grupoPalavra.grupoNome = cursorGruposPalavras.getString(cursorGruposPalavras.getColumnIndex("grupoNome"));
				   grupoPalavra.palavraNome = cursorGruposPalavras.getString(cursorGruposPalavras.getColumnIndex("PalavraNome"));
				   gruposPalavras.add(grupoPalavra);
			   } while (cursorGruposPalavras.moveToNext());
		   }
		 //Carrega dados da tabela tabelaFrases
		   cursorFrases = bancoDados.query(tabelaFrases, camposTabelaFrases, 
				   null, null, null, null, null, null);
		   if (cursorFrases.getCount() > 0){
			   cursorFrases.moveToFirst();
			   frases.clear();
			   do {
				   Frases frase = new Frases();
				   frase.idFrases = cursorFrases.getInt(cursorFrases.getColumnIndex("idFrases"));
				   frase.frases = cursorFrases.getString(cursorFrases.getColumnIndex("frases"));
				   frase.palavrasID = cursorFrases.getInt(cursorFrases.getColumnIndex("palavraID"));
				   frases.add(frase);
			   } while (cursorFrases.moveToNext());
		   }
			  
			   
	   } catch(Exception erro) {
		     Toast.makeText(CadastroConsulta.this, "Erro buscar dados no banco: "+erro.getMessage(), Toast.LENGTH_LONG).show();	    
		     //return false;
	   }
		
	}

	private void desabilitarCampos() {
		etCadastroConsultaPalavras.setEnabled(false);
		etCadastroConsultaDefinicao.setEnabled(false);
		etCadastroConsultaClasses.setEnabled(false);
		etCadastroConsultaTraducao.setEnabled(false);
		etCadastroConsultaPastForm.setEnabled(false);
		etCadastroConsultaPastParticiple.setEnabled(false);
		etCadastroConsultaGrupo.setEnabled(false);
		etCadastroConsultaAddExemplo.setEnabled(false);
		etCadastroConsultaAddExemplo.setVisibility(android.view.View.GONE);
		btCadastroConsultaAddExemplo.setEnabled(false);
		btCadastroConsultaAddExemplo.setVisibility(android.view.View.GONE);
		cbCadastroConsultaIrregular.setEnabled(false);
		lvCadastroConsultaExemplos.setEnabled(false);
		
		etCadastroConsultaPalavras.setTextColor(Color.GRAY);
		etCadastroConsultaDefinicao.setTextColor(Color.GRAY);	
		etCadastroConsultaClasses.setTextColor(Color.GRAY);
		etCadastroConsultaTraducao.setTextColor(Color.GRAY);
		etCadastroConsultaPastForm.setTextColor(Color.GRAY);
		etCadastroConsultaPastParticiple.setTextColor(Color.GRAY);
		etCadastroConsultaGrupo.setTextColor(Color.GRAY);
	}

	private void listener() {
		etCadastroConsultaPalavras.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus){
					buscarDados();
					for (Palavras word : palavras){
						if(!atualizando){
							if (word.palavra.toLowerCase(Locale.ENGLISH).contains(etCadastroConsultaPalavras.getText().toString().toLowerCase(Locale.ENGLISH))){
								etCadastroConsultaPalavras.setError("Palavra ja cadastrada!");
								etCadastroConsultaPalavras.setTextColor(Color.RED);
								erroPalavra=true;
							}  else {
								erroPalavra=false;
								etCadastroConsultaPalavras.setTextColor(Color.BLACK);
							}
						} else {
							if (!etCadastroConsultaPalavras.getText().toString().toLowerCase(Locale.ENGLISH).equals(palavraSelecionada.toLowerCase(Locale.ENGLISH))){
								etCadastroConsultaPalavras.setError("Palavra ja cadastrada!");
								etCadastroConsultaPalavras.setTextColor(Color.RED);
								erroPalavra=true;
							}  else {
								erroPalavra=false;
								etCadastroConsultaPalavras.setTextColor(Color.BLACK);
							}
						}
					}
				}
				
			}
		});
		etCadastroConsultaClasses.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				pegarClassesDefinidas(true);
				abrePopupClasse("Classes Gramaticais", classesGramaticais, classesDefinidasBoolean, "edittext");		
			}
		});
		
		btCadastroConsultaAddExemplo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!etCadastroConsultaAddExemplo.getText().toString().equals("")){
					pegarClassesDefinidas(false);
					classesExemplosString = new String[classesSelecionadas.size()]; //Converte o Array List em String[] para usar no m�todo
							classesExemplosString = classesSelecionadasAntes.toArray(classesExemplosString);
					abrePopupClasse("Escolha em qual classe deseja adicionar o exemplo", 
							classesExemplosString, classesDefinidasBoolean, "exemplos");
				}
			}
		});
		etCadastroConsultaGrupo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buscarDados();			
				gruposSelecionados.clear();
				AlertDialog.Builder opcoes = new AlertDialog.Builder(CadastroConsulta.this);
				opcoes.setTitle("Selecione o(s) Grupo(s)!");
				gruposCadastrados = new String[grupos.size()];
				int z = 0;
				for (Grupos group : grupos){
					gruposCadastrados[z] = group.grupo;
					z++;
				}
				
				boolean[] gruposBoolean = new boolean[classesGramaticais.length]; // Cria o array booleano para passar ao contrutor do popupClasses.setMultiChoiceItems
				String gruposDefinidosNome = etCadastroConsultaGrupo.getText().toString();
				if (!etCadastroConsultaGrupo.equals("")){ //Se o Edit Text Estiver vazio n�o precisa montar array booleano 
					ArrayList<String> gruposSelecionadosAntes = new ArrayList<String>
														(Arrays.asList(gruposDefinidosNome.split(", ")));
						for (int j=0; j < gruposCadastrados.length;j++){
							gruposBoolean[j] = false;	
							for (int i=0; i < gruposSelecionadosAntes.size();i++){	
								if (gruposSelecionadosAntes.get(i).equals(gruposCadastrados[j])){
									gruposBoolean[j] = true; // Define true as classes presentes para marcar o checkbox
									gruposSelecionados.add(j);
								}
							}
						}
				}
				
				opcoes.setMultiChoiceItems(gruposCadastrados, gruposBoolean, new OnMultiChoiceClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						if (isChecked) {
		                    gruposSelecionados.add(which);
		                 } else if (gruposSelecionados.contains(which)) { 
		                	 gruposSelecionados.remove(Integer.valueOf(which));	        	         
		                 }
					}
				});
				opcoes.setPositiveButton("Novo", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AlertDialog.Builder novoGrupo = new AlertDialog.Builder(CadastroConsulta.this);
						novoGrupo.setTitle("Digite o nome do Grupo.");
						etNovoGrupo = new EditText(CadastroConsulta.this);
						etNovoGrupo.setHint("Digite o Nome do grupos");
						novoGrupo.setView(etNovoGrupo);
						novoGrupo.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog, int which) {
								boolean err=false;
								for (Grupos gp : grupos){
									if (gp.grupo.toLowerCase(Locale.ENGLISH).equals(etNovoGrupo.getText().toString().toLowerCase(Locale.ENGLISH))){
										Toast.makeText(CadastroConsulta.this, "ERRO! \nO Grupo j� existe!", Toast.LENGTH_SHORT).show();
									    err = true;
									}
								}
								if (!err){
									gravarGrupos(etNovoGrupo.getText().toString());	
									buscarDados();
								}
							}
						});
						novoGrupo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();	
							}
						});
						novoGrupo.show();
						dialog.cancel();
					}
				});
				opcoes.setNegativeButton("Ok", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						preencheEditTextGrupos(gruposCadastrados, gruposSelecionados);							
					}
				});
				opcoes.setNeutralButton("Cancelar", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();	
					}
				});
				opcoes.show();	
			}
		});
		cbCadastroConsultaIrregular.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (cbCadastroConsultaIrregular.isChecked()){
					etCadastroConsultaPastForm.setVisibility(android.view.View.VISIBLE);
					etCadastroConsultaPastParticiple.setVisibility(android.view.View.VISIBLE);
					tvCadastroConsultaFormaVerbos.setVisibility(android.view.View.VISIBLE);
					tvCadastroConsultaPastForm.setVisibility(android.view.View.VISIBLE);
					tvCadastroConsultaPastParticiple.setVisibility(android.view.View.VISIBLE);
				} else {
					etCadastroConsultaPastForm.setVisibility(android.view.View.GONE);
					etCadastroConsultaPastParticiple.setVisibility(android.view.View.GONE);
					tvCadastroConsultaFormaVerbos.setVisibility(android.view.View.GONE);
					tvCadastroConsultaPastForm.setVisibility(android.view.View.GONE);
					tvCadastroConsultaPastParticiple.setVisibility(android.view.View.GONE);
				}
			}
		});
		lvCadastroConsultaExemplos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					final int posicao = position;
					AlertDialog.Builder opcoes = new AlertDialog.Builder(CadastroConsulta.this);
					opcoes.setTitle("Edite a Frase?");
					opcoes.setMessage("Se quiser APAGAR o item basta clickar na frase e segurar.");
					
					final EditText et = new EditText(CadastroConsulta.this);
					String frase = classesExemplos.get(position).toString();
					final ArrayList<String> classeFrase = new ArrayList<String>
					(Arrays.asList(frase.split(": \n")));
					et.setText(classeFrase.get(1).toString());
					
					opcoes.setView(et);
					opcoes.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (!et.getText().toString().equals("")){
								classesExemplos.remove(posicao);
								classesExemplos.add(classeFrase.get(0).toString()+": \n"
										+et.getText().toString());
								criarListaExemplo();								
								Toast.makeText(CadastroConsulta.this, "Exemplo editado com sucesso!", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(CadastroConsulta.this, "Nenhum Exemplo modificado. Campo n�o pode ficar vazio!", Toast.LENGTH_SHORT).show();
							}
						}
					});
					opcoes.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();	
						}
					});
					opcoes.show();				
			}
		});
		lvCadastroConsultaExemplos.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int posicao2 = position;
				AlertDialog.Builder opcoes = new AlertDialog.Builder(CadastroConsulta.this);
				opcoes.setTitle("Deseja apagar o item?");
				opcoes.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						classesExemplos.remove(posicao2);
						criarListaExemplo();
						Toast.makeText(CadastroConsulta.this, "Exemplo removido com sucesso!", Toast.LENGTH_SHORT).show();
					}
				});
				opcoes.setNegativeButton("N�o", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();	
					}
				});
				opcoes.show();				
				return true;
			}
		});
	}
	
	private void preencheEditTextGrupos(String[]items, ArrayList<Integer> classes){
		String gruposFinal="";
		Collections.sort(classes);
		if (!classes.isEmpty()){
			for (int i=0; i < classes.size();i++){ //Monta String pro EditText
				gruposFinal += items[Integer.valueOf(classes.get(i))];
				gruposFinal +=", ";
			}
		} else {
			gruposFinal +=", ";
		}
		etCadastroConsultaGrupo.setText(gruposFinal.substring(0, gruposFinal.length()-2));
	}

	private void preencheEditTextClasses(ArrayList<Integer> classes){
		String classesFinal="";
		Collections.sort(classes);
		if (!classes.isEmpty()){
			etCadastroConsultaAddExemplo.setEnabled(true);
			btCadastroConsultaAddExemplo.setEnabled(true);
			for (int i=0; i < classes.size();i++){ //Monta String pro EditText
				if (classesGramaticais[Integer.valueOf(classes.get(i))].equals("Verb"))
					cbCadastroConsultaIrregular.setVisibility(android.view.View.VISIBLE);
				classesFinal += classesGramaticais[Integer.valueOf(classes.get(i))];
				classesFinal +=", ";
			}
		} else {
			classesFinal +=", ";
		}
		etCadastroConsultaClasses.setText(classesFinal.substring(0, classesFinal.length()-2));
	}
	
	private void abrePopupClasse(String titulo, String[] classes, boolean[] marcados, final String qual) {// Cria popup de op��es de classes e exemplos
		AlertDialog.Builder popupClasses = new AlertDialog.Builder(CadastroConsulta.this); 
		popupClasses.setTitle(titulo);
		popupClasses.setMultiChoiceItems(classes, marcados, 
				new OnMultiChoiceClickListener() {					

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if (isChecked) {
                    classesSelecionadas.add(which);
                    if (qual.equals("exemplos")){
	            	 	criaCamposExemplo(classesSelecionadas); //Cria a lista com exemplos
	            	 	dialog.cancel(); //Selecionar apenas a classe equivalente.
                    }
                 } else if (classesSelecionadas.contains(which)) { 
                   	 classesSelecionadas.remove(Integer.valueOf(which));	        	         
                 }
			}
		});
		if (qual.equals("edittext")){
			popupClasses.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	             @Override
	             public void onClick(DialogInterface dialog, int id) {
	            	 preencheEditTextClasses(classesSelecionadas); //Coloca as Classes Gramaticasi no EditText
	             }
	         });
	        popupClasses.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	             @Override
	             public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();	                   
	             }
	         });
		}
		popupClasses.show();		
	}
	
	private void pegarClassesDefinidas(boolean run){
		classesSelecionadas.clear(); // Apaga o vetor possibilitando seu uso para os 2 AlertDialog com Checkbox
		classesDefinidasBoolean = new boolean[classesGramaticais.length]; // Cria o array booleano para passar ao contrutor do popupClasses.setMultiChoiceItems
		String classesDefininasNome = etCadastroConsultaClasses.getText().toString();
		if (!classesDefininasNome.equals("")){ //Se o Edit Text Estiver vazio n�o precisa montar array booleano 
			classesSelecionadasAntes = new ArrayList<String>
												(Arrays.asList(classesDefininasNome.split(", ")));
			if (run){ //Vai rodar somente quanto for necess�rio
				for (int j=0; j < classesGramaticais.length;j++){
					classesDefinidasBoolean[j] = false;	
					for (int i=0; i < classesSelecionadasAntes.size();i++){	
						if (classesSelecionadasAntes.get(i).equals(classesGramaticais[j])){
							classesDefinidasBoolean[j] = true; // Define true as classes presentes para marcar o checkbox
							classesSelecionadas.add(j); //reconstroi o vetor zerado pelo pegarClassesDefinidas() para sempre manter o valor correto
						}
					}
				}
			}
		}
	}
	
	private void criaCamposExemplo(ArrayList<Integer> classes) { //Monta a lista dos Exemplos.
		if (!classes.isEmpty()){
			String addFrase = classesSelecionadasAntes.get(classes.get(0))+": \n"
					+etCadastroConsultaAddExemplo.getText().toString();
			if (!classesExemplos.contains(addFrase)){
				classesExemplos.add(addFrase);
				criarListaExemplo();
				etCadastroConsultaAddExemplo.setText("");
				Toast.makeText(CadastroConsulta.this, "Exemplo adicionado com sucesso!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(CadastroConsulta.this, "A Frase J� Existe", Toast.LENGTH_SHORT).show();
				etCadastroConsultaAddExemplo.requestFocus();
			}
		}	
	}
	
	private void criarListaExemplo(){
		Collections.sort(classesExemplos); //Ordena para deixar todos agrupados.
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
							android.R.layout.simple_list_item_1, classesExemplos);
		lvCadastroConsultaExemplos.setAdapter(adapter);
		getListViewSize(lvCadastroConsultaExemplos); // Define tamanho da lista.		
	}
	
    public static void getListViewSize(ListView myListView) { //Redefine tamanho da Lista para Usa-la em ScrollView
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        //Log.i("height of listItem:", String.valueOf(totalHeight));
    }
    
    public void abreouCriaBanco() {
		 try {		   
			   //cria ou abre o banco de dados
			   bancoDados = openOrCreateDatabase(nomeBanco, MODE_PRIVATE, null);
		   }
		   catch(Exception erro)
		   {
			   Log.i("Erro Banco", "Erro ao abrir ou criar o banco: "+erro.getMessage());
		   }
	}
    
    public boolean gravarPalavras(){
    	try {
			   String sqlPalavra="INSERT INTO "+tabelaPalavras+" (palavra, definicao, classes, traducao, irregular, pastform, pastparticiple) values ('"+
					   		etCadastroConsultaPalavras.getText().toString()+
					   		"', '"+etCadastroConsultaDefinicao.getText().toString()+
					   		"', '"+etCadastroConsultaClasses.getText().toString()+
					   		"', '"+etCadastroConsultaTraducao.getText().toString()+
					   		"', '"+cbCadastroConsultaIrregular.isChecked()+
					   		"', '"+etCadastroConsultaPastForm.getText().toString()+
					   		"', '"+etCadastroConsultaPastParticiple.getText().toString()+"')";		   
			   bancoDados.execSQL(sqlPalavra);	
			   Log.i("Sucesso: ", "Dados Gravados com Sucesso!");
			   return true;
		 } catch(Exception erro) {
			 Log.i("Erro: ", "Erro ao gravar dados no banco: "+erro.getMessage());
			 return false;
			  
		 }
    }
    
    public void gravarGrupos(String grupo){
    	try {
			   String sqlGrupos="INSERT INTO "+tabelaGrupos+" (grupo) values ('"+grupo+"')";		   
			   bancoDados.execSQL(sqlGrupos);	
			   Toast.makeText(CadastroConsulta.this, "Grupo Gravado com Sucesso!", Toast.LENGTH_LONG).show();
		 } catch(Exception erro) {
			   Toast.makeText(CadastroConsulta.this, "Erro ao gravar grupo no banco: "+erro.getMessage(), Toast.LENGTH_LONG).show();
			  
		 }
    }
    
    public boolean gravarPalavraGrupos(){
    	boolean resultado = true;
    	if (!etCadastroConsultaGrupo.equals("")){ //Se o Edit Text Estiver vazio n�o precisa montar array booleano
    		apagarPalavraGrupos();
    		String gruposDefinidosNome = etCadastroConsultaGrupo.getText().toString();		 
			ArrayList<String> gruposSelecionados = new ArrayList<String>
												(Arrays.asList(gruposDefinidosNome.split(", ")));
	    	for (int i=0; i < gruposSelecionados.size(); i++){
	    		try {
	    			String sqlGrupos="INSERT INTO "+tabelaGruposPalavras+" (grupoNome, PalavraNome) values ('"+gruposSelecionados.get(i)
		 					   														+"','"+etCadastroConsultaPalavras.getText().toString()+"')";		   
		 			bancoDados.execSQL(sqlGrupos);	
		 			Log.i("Sucesso: ", "Referencia Grupo/Palavra Gravado com Sucesso!");
		 			resultado = true;
	    		} catch(Exception erro) {
	    			Log.i("Erro: ", "Erro ao gravar Referencia Grupo/Palabra no banco: "+erro.getMessage());	
	    			resultado = false;
	    		}
	    	}
    	}
    	return resultado;
    	
    }
    
    public void apagarPalavraGrupos(){
    	buscarDados();
    	String palavraGrupo = "";
    	for (Palavras palavraCadastrada : palavras){
    		if (palavraCadastrada.palavra.equals(etCadastroConsultaPalavras.getText().toString())){
    			palavraGrupo = palavraCadastrada.palavra;
    		}
    	}
	    try {
			   String sqlFrases="DELETE FROM "+tabelaGruposPalavras+" WHERE PalavraNome='"+palavraGrupo+"';";								  
   
			   bancoDados.execSQL(sqlFrases);	
			   Log.i("Sucesso:", "Referencia Grupo/Palavra excluidas");
		 } catch(Exception erro) {
			 Log.i("Erro:", "Referencia Grupo/Palavra n�o excluidas");
				  
		 }
    
    }
    
    public boolean gravarFrases(){
    	boolean resultado = true;
    	buscarDados();
    	apagarFrase();
    	int palavraID = -1;
    	for (Palavras palavraCadastrada : palavras){
    		if (palavraCadastrada.palavra.equals(etCadastroConsultaPalavras.getText().toString())){
    			palavraID = palavraCadastrada.idPalava;
    		}
    	}
    	for (int i = 0; i < classesExemplos.size(); i++){
	    	try {
	    		String sqlFrases="INSERT INTO "+tabelaFrases+" (frases, palavraID) values ('"+classesExemplos.get(i)+
					   																		"', '"+palavraID+"')";		   
			   	bancoDados.execSQL(sqlFrases);	
			   	Log.i("Sucesso:", "Frases Referente a palavraID: "+palavraID+" gravadas");
			   	resultado = true;
			 } catch(Exception erro) {
				Log.i("Erro:", "Frases Referente a palavraID: "+palavraID+" n�o gravadas");		
				resultado = false;
			 }
    	}
    	return resultado;
    }
    
    public void apagarFrase(){
    	buscarDados();
    	int palavraID = -1;
    	for (Palavras palavraCadastrada : palavras){
    		if (palavraCadastrada.palavra.equals(etCadastroConsultaPalavras.getText().toString())){
    			palavraID = palavraCadastrada.idPalava;
    		}
    	}
	    try {
			   String sqlFrases="DELETE FROM "+tabelaFrases+" WHERE palavraID='"+palavraID+"';";								  
   
			   bancoDados.execSQL(sqlFrases);	
			   Log.i("Sucesso:", "Frases Referente a palavraID: "+palavraID+" excluidas");
		 } catch(Exception erro) {
			 Log.i("Erro:", "Frases Referente a palavraID: "+palavraID+" n�o excluidas");
				  
		 }
    
    }
    
    public boolean atualizarPalavras(){
    	try {
			   String sqlPalavra="UPDATE "+tabelaPalavras+" set palavra='"+etCadastroConsultaPalavras.getText().toString()+"', " +
			   								  "definicao='"+etCadastroConsultaDefinicao.getText().toString()+"', "+
			   								  "classes='"+etCadastroConsultaClasses.getText().toString()+"', "+
			   								  "traducao='"+etCadastroConsultaTraducao.getText().toString()+"', "+
			   								  "irregular='"+cbCadastroConsultaIrregular.isChecked()+"', "+
			   								  "pastform='"+etCadastroConsultaPastForm.getText().toString()+"', "+
			   								  "pastparticiple='"+etCadastroConsultaPastParticiple.getText().toString()+"' WHERE  idPalava='"+idPalava+"';";								  
			   bancoDados.execSQL(sqlPalavra);
			   palavraSelecionada = etCadastroConsultaPalavras.getText().toString();
			   Log.i("Sucesso: ", "Dados Atualizados com Sucesso!");
			   return true;
		   }
		   catch(Exception erro) {
			   Log.i("Erro: ", "Erro ao ataualizar dados no banco: "+erro.getMessage());
			  return false;
		   }
    }
    
    public void habilitarCampos(){
    	etCadastroConsultaPalavras.setEnabled(true);
		etCadastroConsultaDefinicao.setEnabled(true);
		etCadastroConsultaClasses.setEnabled(true);
		etCadastroConsultaTraducao.setEnabled(true);
		etCadastroConsultaPastForm.setEnabled(true);
		etCadastroConsultaPastParticiple.setEnabled(true);
		etCadastroConsultaGrupo.setEnabled(true);
		etCadastroConsultaAddExemplo.setEnabled(true);
		etCadastroConsultaAddExemplo.setVisibility(android.view.View.VISIBLE);
		btCadastroConsultaAddExemplo.setEnabled(true);
		btCadastroConsultaAddExemplo.setVisibility(android.view.View.VISIBLE);
		cbCadastroConsultaIrregular.setEnabled(true);
		lvCadastroConsultaExemplos.setEnabled(true);
		
		etCadastroConsultaPalavras.setTextColor(Color.BLACK);
		etCadastroConsultaDefinicao.setTextColor(Color.BLACK);	
		etCadastroConsultaClasses.setTextColor(Color.BLACK);
		etCadastroConsultaTraducao.setTextColor(Color.BLACK);
		etCadastroConsultaPastForm.setTextColor(Color.BLACK);
		etCadastroConsultaPastParticiple.setTextColor(Color.BLACK);
		etCadastroConsultaGrupo.setTextColor(Color.BLACK);
		
		Toast.makeText(CadastroConsulta.this, "Voc� pode editar os campos agora. ", Toast.LENGTH_LONG).show();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.main, menu);		
		add = menu.findItem(R.id.menu_add);
		save = menu.findItem(R.id.menu_save);
		edit = menu.findItem(R.id.menu_edit);
		cancel = menu.findItem(R.id.menu_cancel);
		add.setVisible(false);
		cancel.setVisible(false);
		save.setVisible(false);
			
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_settings) {
			return true;
		}
		if (id == R.id.menu_add){
			Intent iTelaCadastroConsulta = new Intent(this, CadastroConsulta.class);
			startActivity(iTelaCadastroConsulta);
			return true;
		}
		if (id == R.id.menu_edit){
			atualizando=true;
			edit.setVisible(false);
			save.setVisible(true);
			cancel.setVisible(true);
			habilitarCampos();
			return true;
		}
		if (id == R.id.menu_save){
			if (!etCadastroConsultaPalavras.getText().toString().equals("")){
				if (!erroPalavra){
					if (atualizando){
						if (atualizarPalavras() && gravarPalavraGrupos() && gravarFrases()){
							Toast.makeText(CadastroConsulta.this, "Dados Atualizados com Sucesso!", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(CadastroConsulta.this, "Erro ao Atualizar Dados!", Toast.LENGTH_LONG).show();
						}
							
						/*atualizarPalavras();
						gravarPalavraGrupos();
						gravarFrases();*/
					} else {
						if (gravarPalavras() && gravarPalavraGrupos() && gravarFrases()){
							Toast.makeText(CadastroConsulta.this, "Dados Gravados com Sucesso!", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(CadastroConsulta.this, "Erro ao Gravar Dados!", Toast.LENGTH_LONG).show();
						}
						/*gravarPalavras();
						gravarPalavraGrupos();
						gravarFrases();*/
					}
					edit.setVisible(true);
					cancel.setVisible(false);
					save.setVisible(false);
					desabilitarCampos();
				} else {
					Toast.makeText(CadastroConsulta.this, "Corriga os erros antes de salvar!", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(CadastroConsulta.this, "O campo palavra n�o pode estar vazio!", Toast.LENGTH_LONG).show();
			}
			return true;
		}
		if (id == R.id.menu_cancel){
			atualizando=false;
			edit.setVisible(true);
			save.setVisible(false);
			desabilitarCampos();
			cancel.setVisible(false);
			return true;
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onPrepareOptionsMenu (Menu menu){
		Intent IDadosRecebidos = getIntent();
		if (IDadosRecebidos.getStringExtra("Palavra") != null){	
			if(!save.isVisible()){
				edit.setVisible(true);
				save.setVisible(false);
			}
		} else {
			save.setVisible(true);
			edit.setVisible(false);
		}
		
		return true;		
	}
}
