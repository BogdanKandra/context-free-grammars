package temaTC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;
import org.apache.commons.lang3.StringUtils; // pentru metoda substring

public class Gramatica {
	
	ArrayList<String> neterminale;
	String terminale;
	ArrayList<Productie> productii;
	ArrayList<Set<String>> follows; // Va contine multimile FOLLOWk asociate fiecarui neterminal
	
	public Gramatica(ArrayList<String> neterminalele, String terminalele, ArrayList<Productie> productiile){
		
		neterminale = neterminalele;
		terminale = terminalele;
		productii = productiile;
		follows = new ArrayList<>();
	}
	
	public Gramatica(){
		this(new ArrayList<String>(), "", new ArrayList<Productie>());
	}
	
	public void creeazaFollowSets(){
		
		// Adaug cate o multime pentru fiecare neterminal
		for(int i = 0; i < neterminale.size(); i++){
			
			Set<String> followA = new HashSet<>();
			follows.add(followA);
		}
		
		follows.get(0).add("&"); // Initializez FOLLOWk(S) = {&}
	}
	
	public ArrayList<Productie> getProductii(){
		return productii;
	}
			
	// Verifica daca un cuvant contine neterminale in componenta lui
	public boolean contineNeterminale(String cuvant){
		
		for(int i = 0; i < cuvant.length(); i++)
			if(i + 1 < cuvant.length() && cuvant.charAt(i + 1) == '\''){
				if(neterminale.contains("" + cuvant.charAt(i) + "'"))
					return true;
				i++;
			} else
				if(neterminale.contains("" + cuvant.charAt(i)))
					return true;
		
		return false;
	}
	
	// Intoarce urmatorul neterminal din componenta cuvantului dat ca argument, pornind de la pozitia data
	public String getUrmatorulNeterminal(String cuvant, int pozitie){
		
		for(int i = pozitie; i < cuvant.length(); i++)
			if(i + 1 < cuvant.length() && cuvant.charAt(i + 1) == '\''){
				if(neterminale.contains("" + cuvant.charAt(i) + "'"))
					return String.valueOf(cuvant.charAt(i) + "'");
				i++;
			} else
				if(neterminale.contains("" + cuvant.charAt(i)))
					return String.valueOf(cuvant.charAt(i));
		
		return "";
	}
	
	// Intoarce primul neterminal din componenta unui cuvant (stim ca exista cel putin unul)
	public String getPrimulNeterminal(String cuvant){
		
		return this.getUrmatorulNeterminal(cuvant, 0);
	}
	
	// Intoarce o lista formata din neterminalele din compozitia unui cuvant
	public ArrayList<String> getNeterminale(String cuvant){
		
		ArrayList<String> rez = new ArrayList<>();
		
		for(int i = 0; i < cuvant.length(); i++)			
			if(i + 1 < cuvant.length() && cuvant.charAt(i + 1) == '\''){
				if(neterminale.contains("" + cuvant.charAt(i) + "'"))
					rez.add(String.valueOf(cuvant.charAt(i) + "'"));
				i++;
			} else
				if(neterminale.contains("" + cuvant.charAt(i)))
					rez.add(String.valueOf(cuvant.charAt(i)));
		
		return rez;
	}
	
	// Intoarce indicele neterminalului dat ca argument, in lista de neterminale ale gramaticii
	public int getIndexNeterminal(String neterminal){
		
		return neterminale.indexOf(neterminal);
	}
	
	// Intoarce indicele neterminalului dat ca argument, in String-ul dat ca argument
	public int getIndexNeterminal(String neterminal, String membruProductie){
		
		return membruProductie.indexOf(neterminal);
	}
	
	// Numara terminalele care apar pana la intalnirea primului neterminal, intr-un cuvant
	public int numarTerminalePrefix(String cuvant){
		
		int nr = 0;
		
		for(int i = 0; i < cuvant.length(); i++)			
			if(terminale.contains("" + cuvant.charAt(i)))
				nr++;
			else break;
			
		return nr;
	}

	// Elimina duplicatele dintr-o lista de productii
	public static ArrayList<Productie> eliminareDuplicate(ArrayList<Productie> prods){
		
		for(int i = 0; i < prods.size() - 1; i++)
			for(int j = i + 1; j < prods.size(); j++)
				if((prods.get(i).m_stang.equals(prods.get(j).m_stang)) && (prods.get(i).m_drept.equals(prods.get(j).m_drept))){
					prods.remove(i);
					i--; // ca sa nu pierd o iteratie din cauza stergerii productiei
					j--; //
					break;
				}
		
		return prods;
	}
	
	// O gramatica este recursiva imediat la stanga daca are o productie
	// A carui membru stang coincide cel mai din stanga neterminal din membrul drept
	public boolean esteRecursivaImediat(){
		
		for(Productie prod : productii)
			// Daca productia e de forma A -> Aa sau chiar A' -> A'a
//	LOL		if(prod.m_stang.equals(String.valueOf(prod.m_drept.charAt(0))) || (prod.m_drept.length() >= 2 && prod.m_stang.equals(String.valueOf(prod.m_drept.charAt(0) + prod.m_drept.charAt(1))))){
			if(prod.m_drept.startsWith(prod.m_stang)){
					System.out.println("Gramatica introdusa este recursiva imediat la stanga\n");
					return true;
			}
		
		System.out.println("Gramatica introdusa NU este recursiva imediat la stanga\n");
		return false;
	}
	
	// Inlocuieste productiile de tipul A -> Aa1 | Aa2 | ... | Aam | b1 | b2 | ... | bn ; m,n >= 1 cu
	// A -> b1A' | b2A' | ... | bnA'
	// A'-> a1A' | a2A' | ... | amA' | & , unde A' este neterminal nou si & = lambda
	public void eliminareRecursivitateImediata(){
		
		ArrayList<Productie> productiiToAdd = new ArrayList<>();
		
		for(int i = 0; i < productii.size(); i++){
			
			Productie prod = new Productie();
			prod = productii.get(i);
			
			// Daca productia e de forma A -> Aa sau chiar A' -> A'a
			if(prod.m_drept.startsWith(prod.m_stang)){
				
				for(int j = 0; j < productii.size(); j++){
										
					Productie prodj = new Productie();
					prodj = productii.get(j);
					
					// Daca lista de neterminale nu contine versiunea cu apostrof, o adaug
					if(! neterminale.contains(prod.m_stang + "'"))
						neterminale.add(prod.m_stang + "'");
					
					if((prodj.m_stang.equals(prod.m_stang)) && !(prodj.m_drept.startsWith(prod.m_stang))){ // A -> b, b nu incepe cu A
						Productie pr1 = new Productie();
						pr1.m_stang = prod.m_stang;
						pr1.m_drept = prodj.m_drept + prod.m_stang + "'";
						
						productiiToAdd.add(pr1); // A -> bA'
						
						Productie pr2 = new Productie();
						pr2.m_stang = prod.m_stang + "'";
						pr2.m_drept = prod.m_drept.substring(1) + prod.m_stang + "'";
						
						productiiToAdd.add(pr2); // A' -> aA'
						
						Productie pr3 = new Productie();
						pr3.m_stang = prod.m_stang + "'";
						pr3.m_drept = "&";
						
						productiiToAdd.add(pr3); // A' -> lambda
					}
				}
				
				productii.remove(prod);
				i--; // ca sa nu pierd o iteratie din cauza stergerii productiei
			}
		}
		
		eliminareDuplicate(productiiToAdd);
		productii.addAll(productiiToAdd);
		
//		productii.removeIf(e -> e.m_stang.equals(e.m_drept.substring(0, 1))); // Java 8
	}
	
	// Elimina doar recursivitatile imediate care incep cu neterminalul dat ca argument
	public void eliminareRecursivitateImediata(String neterminal){
		
		ArrayList<Productie> productiiToAdd = new ArrayList<>();
		
		for(int i = 0; i < productii.size(); i++){
			
			Productie prod = new Productie();
			prod = productii.get(i);
			
			if(prod.m_drept.startsWith(prod.m_stang)) // Productie recursiva imediat la stanga
				if(prod.m_stang.equals(neterminal)){ // Productia contine neterminalul cu care s-a apelat metoda
				
					for(int j = 0; j < productii.size(); j++){
						
						Productie prodj = new Productie();
						prodj = productii.get(j);
						
						// Daca lista de neterminale nu contine versiunea cu apostrof, o adaug
						if(! neterminale.contains(prod.m_stang + "'"))
							neterminale.add(prod.m_stang + "'");
						
						if((prodj.m_stang.equals(prod.m_stang)) && !(prodj.m_drept.startsWith(prod.m_stang))){ // A -> b, b nu incepe cu A
							
							Productie pr1 = new Productie();
							pr1.m_stang = prod.m_stang;
							pr1.m_drept = prodj.m_drept + prod.m_stang + "'";
							
							productiiToAdd.add(pr1); // A -> bA'
							
							Productie pr2 = new Productie();
							pr2.m_stang = prod.m_stang + "'";
							pr2.m_drept = prod.m_drept.substring(1) + prod.m_stang + "'";
							
							productiiToAdd.add(pr2); // A' -> aA'
							
							Productie pr3 = new Productie();
							pr3.m_stang = prod.m_stang + "'";
							pr3.m_drept = "&";
							
							productiiToAdd.add(pr3); // A' -> lambda
						}
					}
					
					productii.remove(prod);
					i--; // ca sa nu pierd o iteratie din cauza stergerii productiei
				}
		}
		
		eliminareDuplicate(productiiToAdd);
		productii.addAll(productiiToAdd);
	}
	
	// Inlocuieste productiile de tipul Ai -> Aj_ceva, unde Aj -> b1 | b2 | ... | bsj, cu
	// Ai -> b1_ceva | b2_ceva | ... | bsj_ceva, eliminand pentru fiecare j si recursivitatile imediate care incep cu Ai
	public void eliminareRecursivitate(){
		
		ArrayList<Productie> prodsToAdd = new ArrayList<>();
		
		for(int i = 0; i < neterminale.size(); i++)
			for(int j = 0; j <= i - 1; j++){
				
				prodsToAdd.clear();
				
				for(int k = 0; k < productii.size(); k++)
					
					// Fiecare productie Ai -> Aj_ceva
					if((productii.get(k).m_stang.equals(neterminale.get(i))) && (productii.get(k).m_drept.startsWith(neterminale.get(j)))){
						
						for(int t = 0; t < productii.size(); t++)
							
							// Fiecare productie Aj -> b1 | b2 | ... | bsj
							if(productii.get(t).m_stang.equals(neterminale.get(j))){
								
								Productie p = new Productie();
								p.m_stang = productii.get(k).m_stang; // Ai
								p.m_drept = productii.get(t).m_drept; // b1
								if(productii.get(k).m_drept.charAt(1) == '\'')
									p.m_drept += productii.get(k).m_drept.substring(2); // ceva
								else p.m_drept += productii.get(k).m_drept.substring(1); // ceva
								
								prodsToAdd.add(p);
							}
						
						productii.remove(k);
						k--; // ca sa nu pierd o iteratie din cauza stergerii
					}
				productii.addAll(prodsToAdd);
				this.eliminareRecursivitateImediata(neterminale.get(i));
			}
	}
	
	// Citeste elementele unei gramatici dintr-un fisier txt
	// Presupun ca neterminalele citite initial nu contin apostrof
	public void citireGramatica(Scanner in){
		
		int nrNeterm = in.nextInt();
		
		for(int i = 1; i <= nrNeterm; i++){
			neterminale.add(in.next());
		}
		
		int nrTerm = in.nextInt();
		
		for(int i = 1; i <= nrTerm; i++){
			terminale += in.next();
		}
		
		int nrProductii = in.nextInt();
		
		for(int i = 1; i <= nrProductii; i++){
			
			Productie prod = new Productie();
			int nrSimb = in.nextInt();
			
			prod.m_stang = in.next();
			for(int j = 1; j <= nrSimb; j++){
				prod.m_drept += in.next();
			}
			
			productii.add(prod);
		}
		
		System.out.println("Citirea a fost facuta cu succes!\n");
	}
	
	// Afiseaza toate elementele unei gramatici
	public void afisareGramatica(){
		
		System.out.println("Gramatica are " + neterminale.size() + " neterminale.\nAcestea sunt: " + neterminale);
		System.out.println("Gramatica are " + terminale.length() + " terminale.\nAcestea sunt: " + terminale);
		System.out.println("Simbolul initial este " + neterminale.get(0));
		System.out.println("Productiile sunt:");
		for(Productie p : productii)
			p.afisare();
		System.out.println('\n');
	}
	
	// Citeste un cuvant peste alfabetul gramaticii G; arunca exceptie daca cuvantul contine litere care nu sunt in alfabet
	public String citesteCuvant(Scanner cons) throws InputMismatchException{
		
		// Pun terminalele si neterminalele intr-o singura lista
		ArrayList<String> alfabet = new ArrayList<>();
		alfabet.addAll(neterminale);
		for(int i = 0; i < terminale.length(); i++)
			alfabet.add(String.valueOf(terminale.charAt(i)));
		
		String cuvant = cons.nextLine();
		
		// Daca litera curenta a cuvantului (cu apostrof sau fara) nu se afla in alfabet, arunc exceptie
		for(int i = 0; i < cuvant.length(); i++)
			if((i + 1 < cuvant.length() && cuvant.charAt(i + 1) == '\'')){
				if(!alfabet.contains("" + cuvant.charAt(i) + "'"))
					throw new InputMismatchException("Trebuie sa introduceti un cuvant peste alfabetul gramaticii!");
				i++;
			} else
				if(!alfabet.contains("" + cuvant.charAt(i)))
					throw new InputMismatchException("Trebuie sa introduceti un cuvant peste alfabetul gramaticii!");
		
		return cuvant;
	}
	
	// Inlocuieste neterminalul cel mai din stanga al string-ului dat ca argument, cu productia corespunzatoare
	public String derivareStanga(String cuvant, Productie P){
		
		String cuvantDerivat = "";
		boolean ulciorLaApa = true;
		
		for(int i = 0; i < cuvant.length(); i++){
			
			if(terminale.contains("" + cuvant.charAt(i))){ // litera este terminal, deci o adaugam la rezultat
				cuvantDerivat += cuvant.charAt(i);
			} else{ // litera este neterminal
				if(ulciorLaApa){ // aplicam productia P doar pentru primul neterminal
					if(!P.m_drept.equals("&")){ // Daca membrul drept al productiei nu e lambda, il adaugam la cuvantul derivat
						cuvantDerivat += P.m_drept;
						if(P.m_stang.contains("'"))
							i++;
					} else{
						if(cuvant.length() == 1){
							cuvantDerivat += P.m_drept;
						}
						if(P.m_stang.contains("'"))
							i++;
					}
					
					ulciorLaApa = false; // ulciorul nu merge de multe ori la apa;
				} else{
					cuvantDerivat += cuvant.charAt(i);
				}
			}
		}
		
		return cuvantDerivat;
	}
	
	
	// Calculeaza FIRSTk(w), unde w este un cuvant peste alfabetul gramaticii
	// Se aplica productiile gramaticii asupra neterminalelor care apar, de la stanga la dreapta,
	// pana cand primele k caractere ale cuvantului obtinut prin derivare sunt terminale,
	// pentru toate derivarile posibile
	public Set<String> First(int k, String cuvant){
		
		Set<String> rezultat = new HashSet<>();
		ArrayList<String> cuvinte = new ArrayList<>();
		
		cuvinte.add(cuvant);
		
		for(int i = 0; i < cuvinte.size(); i++){
			
			if((numarTerminalePrefix(cuvinte.get(i)) >= k) || (contineNeterminale(cuvinte.get(i)) == false)){				
				rezultat.add(StringUtils.substring(cuvinte.get(i),0,k)); // Adaug doar primele k litere
			} else{
				
				String s = getPrimulNeterminal(cuvinte.get(i));
				String cuvantDerivat;
				
				for(Productie p : productii)
					if(p.m_stang.equals(s)){
						
						cuvantDerivat = derivareStanga(cuvinte.get(i), p);
						cuvinte.add(cuvantDerivat);
					}
			}
		}
		
		return rezultat;
	}
	
	
	// Calculeaza si intoarce FOLLOWk(A), unde A este un neterminal
	// FOLLOWk(S) = {&};
	// Cat timp se adauga noi siruri, pentru fiecare productie A -> uBv din P,
	// FOLLOWk(B) += FIRSTk(v . FOLLOWk(A)), unde . reprezinta concatenare
	public Set<String> Follow(int k, String neterminal){
		
		boolean added = true; // Va spune cand sa ne oprim din calcul
		creeazaFollowSets();
		
		while(added){ // Repetam cat timp se adauga noi siruri in multimi
			
			added = false; // Redevine true doar daca se adauga ceva in multimi
			
			for(Productie P : productii)
				
				if(contineNeterminale(P.m_drept)){
					
					ArrayList<String> neterms = getNeterminale(P.m_drept);
					// Pentru fiecare neterminal care apare in productia P
					for(int i = 0; i < neterms.size(); i++){
						
						// indicele neterminalului curent in membrul drept al productiei
						int indexOrigine = getIndexNeterminal(String.valueOf(neterms.get(i)), P.m_drept);
						// indicele neterminalului curent in String-ul de neterminale
						int indexDestinatie = getIndexNeterminal(String.valueOf(neterms.get(i)));
						// ceea ce urmeaza dupa neterminalul curent in productie
						String v;
						if(!P.m_drept.substring(indexOrigine + 1).equals("'"))
							v = P.m_drept.substring(indexOrigine + 1);
						else
							v = P.m_drept.substring(indexOrigine + 2);
						// indicele membrului stang al productiei, in String-ul de neterminale
						int indexMembruStang = getIndexNeterminal(P.m_stang);
						
						Set<String> deAdaugat = new HashSet<>();
						Set<String> argumentFIRSTk = new HashSet<>();
						
						for(String s : follows.get(indexMembruStang)){
							if(!s.equals("&"))
								argumentFIRSTk.add(v + s);
							else
								argumentFIRSTk.add(v);
						}
						
						for(String s : argumentFIRSTk){
							deAdaugat.addAll(First(k,s));
						}
						
						int sizeBefore = follows.get(indexDestinatie).size();
						follows.get(indexDestinatie).addAll(deAdaugat);
						int sizeAfter = follows.get(indexDestinatie).size();
						
						if(!(sizeAfter - sizeBefore == 0)){
							added = true;
						}
					}
				}
		}
		
		int indexFollowRezultat = getIndexNeterminal(neterminal);
		return follows.get(indexFollowRezultat);
	}
}
