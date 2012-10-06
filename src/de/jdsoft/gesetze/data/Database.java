package de.jdsoft.gesetze.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.loopj.android.http.JsonHttpResponseHandler;

import de.jdsoft.gesetze.data.helper.Composer;
import de.jdsoft.gesetze.network.RestClient;

public class Database {
	public static final String TAG = Cached.class.getSimpleName();
	
	public static List<Pair<String, List<Composer>>> getAllData() {
		List<Pair<String, List<Composer>>> res = new ArrayList<Pair<String, List<Composer>>>();
		
		for (int i = 0; i < 4; i++) {
			res.add(getOneSection(i));
		}
		
		return res;
	}
	
	public static List<Composer> getFlattenedData() {
		 List<Composer> res = new ArrayList<Composer>();
		 
		 for (int i = 0; i < 4; i++) {
			 res.addAll(getOneSection(i).second);
		 }
		 
		 return res;
	}
	
	public static Pair<Boolean, List<Composer>> getRows(int page) {
		List<Composer> flattenedData = getFlattenedData();
		if (page == 1) {
			return new Pair<Boolean, List<Composer>>(true, flattenedData.subList(0, 5));
		} else {
			return new Pair<Boolean, List<Composer>>(page * 5 < flattenedData.size(), flattenedData.subList((page - 1) * 5, Math.min(page * 5, flattenedData.size())));
		}
	}
	
	public static Pair<String, List<Composer>> getOneSection(int index) {
	
		String[] sectionTitles = {"A", "B", "C", "D"};
		Composer[][] composerss = {
			{
				new Composer("A/KAE", "Ausführungsanordnung zur Konzessionsabgabenanordnung "),
				new Composer("AABG", "Gesetz zur Begrenzung der Arzneimittelausgaben der gesetzlichen Krankenversicherung"),
				new Composer("AAG ", "Gesetz über den Ausgleich der Arbeitgeberaufwendungen für Entgeltfortzahlung"),
			},
			{
				new Composer("BAA-EinfDV-Saar", "Rechtsverordnung des Präsidenten des Bundesausgleichsamtes zur Einführung von Rechtsverordnungen im Saarland"),
				new Composer("BAAKVBefugV 1973", "Verordnung zur Übertragung der Ermächtigung zum Erlaß von Rechtsverordnungen nach § 10 Satz 1 des Gesetzes über Bausparkassen auf die Bundesanstalt für Finanzdienstleistungsaufsicht"),
				new Composer("BAAZustV", "Verordnung zur Übertragung von Zuständigkeiten nach dem Lastenausgleichsgesetz auf das Bundesausgleichsamt"),
				new Composer("BAB-KAbgV", "Verordnung über Höhe und Erhebung der Konzessionsabgabe für das Betreiben eines Nebenbetriebs an der Bundesautobahn"),
			},
			{
				new Composer("CCDAfrikaÜbk", "Übereinkommen der Vereinten Nationen zur Bekämpfung der Wüstenbildung in den von Dürre und/oder Wüstenbildung schwer betroffenen Ländern, insbesondere in Afrika"),
				new Composer("CCDSekrSitzAbkG", "Gesetz zu dem Abkommen vom 18. August 1998 zwischen der Regierung der Bundesrepublik Deutschland, den Vereinten Nationen und dem Sekretariat des Übereinkommens der Vereinten Nationen zur Bekämpfung der Wüstenbildung über den Sitz des Ständigen Sekretariats des Übereinkommens"),
				new Composer("CDNI", "Übereinkommen über die Sammlung, Abgabe und Annahme von Abfällen in der Rhein- und Binnenschifffahrt "),
				new Composer("ChemBioLackAusbErprobV ", "Verordnung über die Erprobung einer neuen Ausbildungsform für die Berufsausbildung im Laborbereich Chemie, Biologie und Lack"),
				new Composer("ChemBioLackAusbV 2009 ", "Verordnung über die Berufsausbildung im Laborbereich Chemie, Biologie und Lack"),
				new Composer("ChemBiozidMeldeV ", "Verordnung über die Meldung von Biozid-Produkten nach dem Chemikaliengesetz"),
				new Composer("ChemBiozidZulV ", "Verordnung über die Zulassung von Biozid-Produkten und sonstige chemikalienrechtliche Verfahren zu Biozid-Produkten und Biozid-Wirkstoffen"),
			},
			{
				new Composer("DachdArbV 6", "Sechste Verordnung über zwingende Arbeitsbedingungen im Dachdeckerhandwerk "),
				new Composer("DachdAusbV 1998", "Verordnung über die Berufsausbildung zum Dachdecker/zur Dachdeckerin"),
				new Composer("DachdMstrV", "Verordnung über das Meisterprüfungsberufsbild und über die Prüfungsanforderungen in den Teilen I und II der Meisterprüfung im Dachdecker-Handwerk"),
			},
		};
		return new Pair<String, List<Composer>>(sectionTitles[index], Arrays.asList(composerss[index]));
	}
}
