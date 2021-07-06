import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Example of query processing with apache Jena
 * 
 * @author steinbauer@ist.tugraz.at
 * @author sgutierr@ist.tugraz.at
 * @version 1.0
 */
public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	/**
	 * An SPARQL example
	 */
	private static final String query = String.join(System.lineSeparator(),
			"                PREFIX owl:                      <http://www.w3.org/2002/07/owl#>",
			"                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
			"                                                                                                                                       ",
			"                SELECT ?x                                                                                                   ",
			"                WHERE {                                                                                                     ",
			"                {                                                                                                                     ",
			"                  ?x rdfs:subClassOf ?y .                                                                                                                 ",
			"                }}");

	//private static final String document = "http://ias.cs.tum.edu/kb/knowrob.owl#";
	private static final String document = "C:/Users/Andi/Documents/GitHub/IntelligentSystemsPractical/src/main/java/practical_new.owl";
	
	/**
	 * Examples of SPARQL with apache jena
	 * 
	 * @param args none
	 */
	public static void main(String[] args) {

		log.info("Example SPARQL query: ");
		log.info(query);

		log.info("Creates the OWL model");
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

		log.info("Reads the document");
		model.read(document);
//		model.write(System.out);
		
		//String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT * WHERE { }";
		//String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + "SELECT * ";
		//Query query = QueryFactory.create(queryString);
	
		log.info("Query execution is created for the example query");
		QueryExecution qexec = QueryExecutionFactory.create(query, model);

		log.info("Obtains the result set");
		ResultSet results = qexec.execSelect();

		log.info("Iterates over the result set");
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			log.info("Solution: " + sol);
		}

		log.info("Obtain the properties of the model");
		ExtendedIterator<ObjectProperty> properties = model.listObjectProperties();

		log.info("Iterates over the properties");
		while (properties.hasNext()) {
			log.info("Property: " + properties.next().getLocalName());
		}

		log.info("Obtains an iterator over individual resources");
		ExtendedIterator<Individual> individualResources = model.listIndividuals();

		log.info("Iterates over the resources");
		while (individualResources.hasNext()) {
			log.info("Individual resource: " + individualResources.next());
		}

		log.info("Obtains an extended iterator over classes");
		ExtendedIterator<OntClass> classes = model.listClasses();

		log.info("Iterates over the classes");
		while (classes.hasNext()) {
			log.info("Class: " + classes.next().toString());
		}
	}
}