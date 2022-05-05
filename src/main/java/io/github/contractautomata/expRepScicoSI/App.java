package io.github.contractautomata.expRepScicoSI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import CA.CA;
import CA.CAUtil;
import FMCA.FMCA;
import FMCA.Product;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.operations.ChoreographySynthesisOperator;
import io.github.contractautomata.catlib.operations.MSCACompositionFunction;
import io.github.contractautomata.catlib.operations.OrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.requirements.Agreement;
import io.github.contractautomata.catlib.requirements.StrongAgreement;

/**
 * Class reproducing the experiments published at Coordination2021
 * https://doi.org/10.1007/978-3-030-78142-2_14
 * 
 * @author Davide Basile
 *
 */
public class App 
{	
	private static String dir = Paths.get(System.getProperty("user.dir")).getParent()
			+File.separator+"experimentsReproducibilityCoordination2021"
			+File.separator+"resources"+File.separator;

	private static String dir2 = Paths.get(System.getProperty("user.dir")).getParent()
			+File.separator+"experimentsReproducibilityCoordination2021"
			+File.separator+"resources"
			+File.separator+"demoLMCS2020"+File.separator;

    public static void main( String[] args ) throws Exception
    {	

    	System.out.println("Starting the experiments published at Coordination 2021:");

    	Thread keepAlive = new Thread( () ->{
    			try {
					while(true) {
						System.out.println("...");
						Thread.sleep(60000);
					}
					
				} catch (InterruptedException e) {}
    	});
    	keepAlive.start();
    	
    	newVersion();
    	oldVersion();
		System.out.println("Experiments terminated.");
		keepAlive.interrupt();
    }
    
    private static void oldVersion() {
    	System.out.println("The old version of the library, prior to its refactoring, is used. This will take longer...");
    	System.out.println("Importing the automata...");
    	
    	Instant start, stop;
    	long elapsedTime;
    	
    	FMCA client = FMCA.importFromXML(dir2+"Client.mxe");
    	FMCA priviledgedClient = FMCA.importFromXML(dir2+"PriviledgedClient.mxe");
    	FMCA broker = FMCA.importFromXML(dir2+"Broker.mxe");
    	FMCA hotel = FMCA.importFromXML(dir2+"Hotel.mxe");
    	FMCA priviledgedHotel = FMCA.importFromXML(dir2+"PriviledgedHotel.mxe");
    	
    	System.out.println("Computing the compositions...");
    	
    	CA[] arr = new CA[] {client, client, broker, hotel, priviledgedHotel};
    	start = Instant.now();
//    	FMCA a1 = (FMCA) 
    			CAUtil.composition(arr);
		stop = Instant.now();
		elapsedTime = Duration.between(start, stop).toMillis();
		System.out.println("Computing the composition A1 in : " +elapsedTime + " milliseconds");

    	CA[] arr2 = new CA[] {client, priviledgedClient, broker, hotel, hotel};
    	start = Instant.now();
//    	FMCA a2 = (FMCA) 
    			CAUtil.composition(arr2);
		stop = Instant.now();
		elapsedTime = Duration.between(start, stop).toMillis();
		System.out.println("Computing the composition A2 in : " +elapsedTime + " milliseconds");

    	
    	
    	FMCA a1 = FMCA.importFromXML(dir2 + "(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
    	System.out.println(System.lineSeparator()+"Computing the orchestration of A1, this will take several minutes...");
		start = Instant.now();
		FMCA a1_orc = a1.mpc(new Product(new String[0], new String[0]));		
		stop = Instant.now();
		elapsedTime = Duration.between(start, stop).toMillis();
		System.out.println(System.lineSeparator()+"The orchestration of A1 has  been computed in : " +elapsedTime + " milliseconds");

		FMCA a2 = FMCA.importFromXML(dir2 + "(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
    	System.out.println(System.lineSeparator()+"Computing the choreography of A2, this will take several minutes...");
		start = Instant.now();
		FMCA a2_chor = a2.choreography();
		stop = Instant.now();
		elapsedTime = Duration.between(start, stop).toMillis();
		System.out.println("System.lineSeparator()+The choreography of A2 has been computed in : " +elapsedTime + " milliseconds");
		
		
		System.out.println(System.lineSeparator()+"Exporting the synthesised orchestration and choreography...");

		a1_orc.printToFile(dir+"Orc_A1_old.data");
		a2_chor.printToFile(dir+"Chor_A2_old.data");
    }
    
    private static void newVersion() throws IOException {

		AutDataConverter<CALabel>  dmc = new AutDataConverter<>(CALabel::new);
		
    	System.out.println("The new version of the library is now used.");
    	System.out.println("Importing the automata...");
		Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>
  			client = dmc.importMSCA(dir+"Client.data");
		Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>
				priviledgedClient = dmc.importMSCA(dir+"PriviledgedClient.data");
		Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>
				broker = dmc.importMSCA(dir+"Broker.data");
		Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>
				hotel = dmc.importMSCA(dir+"Hotel.data");
		Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>
				priviledgedHotel = dmc.importMSCA(dir+"PriviledgedHotel.data");
    	
    	List<Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>>
				l1 = Arrays.asList(client,client,broker,hotel,priviledgedHotel);
		List<Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>>
				l2 = Arrays.asList(client,priviledgedClient,broker,hotel,hotel);

		Instant start = Instant.now();
		Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>
				a1 =new MSCACompositionFunction<>(l1,null).apply(100);
		Instant stop = Instant.now();
		long elapsedTime = Duration.between(start, stop).toMillis();
		System.out.println("Computing the composition A1 in : " +elapsedTime + " milliseconds");
		
		start = Instant.now();
		Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>
				a2 =new MSCACompositionFunction<>(l2,null).apply(100);
		stop = Instant.now();
		elapsedTime = Duration.between(start, stop).toMillis();
		System.out.println("Computing the composition A2 in : " +elapsedTime + " milliseconds");
		
		start = Instant.now();
		Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>
				orc_a1 = new OrchestrationSynthesisOperator<String>(new Agreement()).apply(a1);
		stop = Instant.now();
		elapsedTime = Duration.between(start, stop).toMillis();
		System.out.println("Computing the orchestration of A1 in : " +elapsedTime + " milliseconds");
		
		start = Instant.now();
		Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>
				chor_a2 = new ChoreographySynthesisOperator<String>(new StrongAgreement()).apply(a2);
		stop = Instant.now();
		elapsedTime = Duration.between(start, stop).toMillis();
		System.out.println("Computing the choreography of A2 in : " +elapsedTime + " milliseconds");

		System.out.println("Exporting the synthesised orchestration and choreography...");
    	dmc.exportMSCA(dir+"Orc_A1.data", orc_a1);
    	dmc.exportMSCA(dir+"Chor_A2.data", chor_a2);
    }
    

}
