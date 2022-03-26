package io.github.contractautomata.expRepScicoSI;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.operators.ChoreographySynthesisOperator;
import io.github.contractautomata.catlib.operators.MSCACompositionFunction;
import io.github.contractautomata.catlib.operators.OrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.requirements.Agreement;
import io.github.contractautomata.catlib.requirements.StrongAgreement;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Class reproducing the experiments published at Coordination2021
 * https://doi.org/10.1007/978-3-030-78142-2_14
 * 
 * @author Davide Basile
 *
 */
public class App 
{	
    public static void main( String[] args ) throws Exception
    {
    	String dir = Paths.get(System.getProperty("user.dir")).getParent()
				+File.separator+"experimentsReproducibilityCoordination2021"
				+File.separator+"resources"+File.separator;
		AutDataConverter<CALabel>  dmc = new AutDataConverter<>(CALabel::new);
    	

    	System.out.println("Starting the experiments published at Coordination 2021:");
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


		System.out.println("Experiments terminated.");
    }
    

}
