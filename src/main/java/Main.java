import java.util.ArrayList;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;


public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	/**
	 * An SPARQL example
	 */
	private static final String query = String.join(System.lineSeparator(),
			"                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
			"                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
			"                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
			"                                                                                                                                       ",
			"                SELECT ?s  ?r   ?p ?a                                                                            ",
			"                WHERE {                                                                                                     ",
			"                {                                                                                                                     ",
			"                  ?p xmlns:hasState ?s . "
			+ " 				   ?r xmlns:hasParticipant	?p . "
			+ " 				   ?r xmlns:hasRoadAddition	?a . "			                                                                                                                ,
			"                }}");

	//private static final String document = "http://ias.cs.tum.edu/kb/knowrob.owl#";
	private static final String document = "C:/Users/Andi/Documents/GitHub/IntelligentSystemsPractical/src/main/java/practical_new.owl";
	
	/**
	 * Examples of SPARQL with apache jena
	 * 
	 * @param args none
	 */
	public static void main(String[] args) {

		log.info(query);
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		model.read(document);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet results = qexec.execSelect();

		ArrayList<String> participants = new ArrayList<String>();
		ArrayList<String> states = new ArrayList<String>();
		ArrayList<String> roads = new ArrayList<String>();
		ArrayList<String> road_additions = new ArrayList<String>();
		
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			
			if(sol.get("p") != null)
			{
				participants.add(sol.get("p").asNode().getLocalName());
			}
			if(sol.get("s") != null)
			{
				states.add(sol.get("s").asNode().getLocalName());
			}
			if(sol.get("r") != null)
			{
				roads.add(sol.get("r").asNode().getLocalName());
			}
			if(sol.get("a") != null)
			{
				road_additions.add(sol.get("a").asNode().getLocalName());
			}
		}
		
		boolean legal = true;
		for(int i = 0; i < participants.size(); i++)
		{
			if(states.get(i).equals("overtake") && road_additions.get(i).equals("solid_line"))
			{
				legal = false;
				log.info("Overtaking over solid line. Situation is not STVO conform");
			}
			else if(!(states.get(i).equals("stop")) && road_additions.get(i).equals("stop_sign"))
			{
				legal = false;
				log.info("Driving over stop sign. Situation is not STVO conform");
			}
		}
		
		if(legal)
		{
			log.info("Situation is STVO conform");
		}
		
		
//		log.info("Obtain the properties of the model");
//		ExtendedIterator<ObjectProperty> properties = model.listObjectProperties();
//
//		log.info("Iterates over the properties");
//		while (properties.hasNext()) {
//			log.info("Property: " + properties.next().getLocalName());
//		}
//
//		log.info("Obtains an iterator over individual resources");
//		ExtendedIterator<Individual> individualResources = model.listIndividuals();
//
//		log.info("Iterates over the resources");
//		while (individualResources.hasNext()) {
//			log.info("Individual resource: " + individualResources.next());
//		}
//
//		log.info("Obtains an extended iterator over classes");
//		ExtendedIterator<OntClass> classes = model.listClasses();
//
//		log.info("Iterates over the classes");
//		while (classes.hasNext()) {
//			log.info("Class: " + classes.next().toString());
//		}
	}
}