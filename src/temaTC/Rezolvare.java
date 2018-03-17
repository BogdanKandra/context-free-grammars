package temaTC;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Rezolvare {
	
	static Gramatica g = new Gramatica();
	
	public static void test1a_1(){
		
		boolean b = g.esteRecursivaImediat();
		
		if(b == true){
			g.eliminareRecursivitateImediata();
			System.out.println("Gramatica obtinuta dupa eliminarea recursivitatii imediate:\n");
			g.afisareGramatica();
		} else{
			System.out.println("Gramatica introdusa NU este recursiva imediat la stanga\n");
		}
	}
	
	public static void test1a_2(){
		
		g.eliminareRecursivitate();
		Gramatica.eliminareDuplicate(g.productii);
		System.out.println("Gramatica obtinuta dupa eliminarea recursivitatii:\n");		
		g.afisareGramatica();
	}
	
	public static void test1b(Scanner cons){
		
		// Prima data elimin recursivitatea, altfel nu se pot calcula First si Follow
		g.eliminareRecursivitateImediata();
		g.eliminareRecursivitate();
		g.afisareGramatica();
		
		boolean flag = true;
		
		try {
			
			System.out.print("Introduceti numarul k (k >= 1): ");
			char ktemp = cons.next().charAt(0);
			if(!(ktemp >= '1' && ktemp <= '9')){ // Daca nu am citit cifra intre 1 si 9
				throw new InputMismatchException("Trebuie sa introduceti un numar mai mare sau egal cu 1!");
			} else{
				
				System.out.println('\n');
				int k = Character.getNumericValue(ktemp);
				String cuvant;
				String neterminal;

				while(flag){
					
					System.out.println("1. Calcul First_k");
					System.out.println("2. Calcul Follow_k");
					System.out.println("3. Iesire\n");
					
					int option = cons.nextInt();
					
					switch(option){
					
					case 1 : cons.nextLine();
							 
							 System.out.print("Introduceti cuvantul peste gramatica G: ");
							 
							 try {
								 cuvant = g.citesteCuvant(cons);
							 } catch (InputMismatchException e) {
									System.out.println(e.getMessage());
									break;
							 }
							 
							 System.out.println("Cuvant citit cu succes!\n");
							 
							 System.out.print("First_" + k + "(" + cuvant + ") = ");
							 System.out.println(g.First(k, cuvant));
							 System.out.print('\n');
							 break;
					case 2 : cons.nextLine();
					
							 System.out.print("Introduceti neterminalul: ");
							 
							 try{
								 neterminal = cons.next();
								 
								 if(!g.neterminale.contains("" + neterminal)){
									throw new InputMismatchException("Argumentul introdus nu este terminal!");
								 }
								 
								 System.out.print("Follow_" + k + "(" + neterminal + ") = ");
								 System.out.println(g.Follow(k, neterminal));
								 System.out.print('\n');
							 } catch (InputMismatchException e){
								 System.out.println(e.getMessage());
							 }
							 break;
					case 3 : System.out.println("Program terminat");
							 flag = false;
							 break;
					default: System.out.println("Ati introdus o optiune inexistenta! Reintroduceti.\n");
					}
				}
			}
		} catch (InputMismatchException e) {
			System.out.println(e.getMessage());
			test1b(cons);
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		String cale = "input6.txt";
		File f = new File(cale);
		FileInputStream fis = new FileInputStream(f);
		Scanner in = new Scanner(fis);
		Scanner cons = new Scanner(System.in);
		
		g.citireGramatica(in);
		g.afisareGramatica();
		
		// Test pentru eliminarea recursivitatilor la stanga imediate ale unei gramatici
//		test1a_1();
		
		// Test pentru eliminarea recursivitatilor la stanga ale unei gramatici
//		test1a_2();
		
		// Test pentru First_k(w) si Follow_k(w)
		test1b(cons);
		
		cons.close();
		in.close();
		fis.close();
	}
}
