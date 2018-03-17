package temaTC;

public class Productie {
	
	String m_stang;
	String m_drept;
	
	public Productie(String st, String dr){
		
		m_stang = st;
		m_drept = dr;
	}
	
	public Productie(){
		this("", "");
	}
	
	public void afisare(){		
		System.out.println(m_stang + " -> " + m_drept);
	}
}
