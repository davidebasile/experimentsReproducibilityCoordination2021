package io.github.davidebasile.expRepScicoSI;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import contractAutomata.automaton.MSCA;
import contractAutomata.converters.DataConverter;
import contractAutomata.converters.MSCAConverter;
import contractAutomata.operators.ChoreographySynthesisOperator;
import contractAutomata.operators.CompositionFunction;
import contractAutomata.operators.OrchestrationSynthesisOperator;
import contractAutomata.requirements.Agreement;
import contractAutomata.requirements.StrongAgreement;

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
    	String dir = System.getProperty("user.dir")+File.separator+"resources"+File.separator;
    	MSCAConverter dmc = new DataConverter();
    	
    	System.out.println("Importing the automata...");
    	MSCA client = dmc.importMSCA(dir+"Client.data");
    	MSCA priviledgedClient = dmc.importMSCA(dir+"PriviledgedClient.data");
    	MSCA broker = dmc.importMSCA(dir+"Broker.data");
    	MSCA hotel = dmc.importMSCA(dir+"Hotel.data");
    	MSCA priviledgedHotel = dmc.importMSCA(dir+"PriviledgedHotel.data");
    	
    	List<MSCA> l1 = Arrays.asList(client,client,broker,hotel,priviledgedHotel);
		List<MSCA> l2 = Arrays.asList(client,priviledgedClient,broker,hotel,hotel);

		Instant start = Instant.now();
		MSCA a1 =new CompositionFunction().apply(l1,null,100);
		Instant stop = Instant.now();
		long elapsedTime = Duration.between(start, stop).toMillis();
		System.out.println("Computing the composition A1 in : " +elapsedTime + " milliseconds");
		
		start = Instant.now();
		MSCA a2 =new CompositionFunction().apply(l2,null,100);
		stop = Instant.now();
		elapsedTime = Duration.between(start, stop).toMillis();
		System.out.println("Computing the composition A2 in : " +elapsedTime + " milliseconds");
		
		start = Instant.now();
		MSCA orc_a1 = new OrchestrationSynthesisOperator(new Agreement()).apply(a1);
		stop = Instant.now();
		elapsedTime = Duration.between(start, stop).toMillis();
		System.out.println("Computing the orchestration of A1 in : " +elapsedTime + " milliseconds");
		
		start = Instant.now();
		MSCA chor_a2 = new ChoreographySynthesisOperator(new StrongAgreement()).apply(a2);
		stop = Instant.now();
		elapsedTime = Duration.between(start, stop).toMillis();
		System.out.println("Computing the choreography of A2 in : " +elapsedTime + " milliseconds");

		System.out.println("Exporting the synthesised orchestration and choreography...");
    	dmc.exportMSCA(dir+"Orc_A1.data", orc_a1);
    	dmc.exportMSCA(dir+"Chor_A2.data", chor_a2);

    }
    

}
