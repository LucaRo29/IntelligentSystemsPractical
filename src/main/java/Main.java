import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
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
			"                SELECT ?s  ?r                                                                               ",
			"                WHERE {                                                                                                     ",
			"                {                                                                                                                     ",
			"                  ?x xmlns:hasState ?s . "
			+ " 				   ?z xmlns:hasParticipant	?x . "
			+ " 				   ?z xmlns:hasRoadAddition	?r . "			                                                                                                                ,
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

		log.info("Obtains the result set");
		ResultSet results = qexec.execSelect();

		log.info("Iterates over the result set");
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			String participant_state = sol.get("s").asNode().getLocalName() ;
		    String road_addition = sol.get("r").asNode().getLocalName() ;
		 
			//log.info("Solution: " + participant_state + " " + road_addition);
			
			System.out.println("state is: " + participant_state);
			System.out.println("addition is: " + road_addition);
			
			if(participant_state.equals("overtake") && road_addition.equals("solid_line"))
			{
				
				log.info("Situation is not STVO conform");
			}
			else
			{
				log.info("Situation is STVO conform");
			}
			
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