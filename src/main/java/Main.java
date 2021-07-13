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

class Pair {
    int key;
    String value;

    public Pair(int key, String a) {
        this.key = key;
        value = a;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static String query = String.join(System.lineSeparator(),
            "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
            "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
            "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
            "                                                                                      ",
            "                SELECT *                                          ",
            "                WHERE {                                                               ",
            "                  ?r xmlns:hasParticipant	?p .                                          ",
            " 				   ?p xmlns:hasState ?s .                          ",
            " 				 OPTIONAL {  ?r xmlns:hasRoadAddition	?a }.                           ",
            "}");

    //private static final String document = "http://ias.cs.tum.edu/kb/knowrob.owl#";
    private static final String document = "src/main/java/practical_new.owl";


    public static void main(String[] args) {

        log.info(query);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        model.read(document);


        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet results = qexec.execSelect();

        ArrayList<Pair> participants = new ArrayList<Pair>();
        ArrayList<Pair> states = new ArrayList<Pair>();
        ArrayList<Pair> roads = new ArrayList<Pair>();
        ArrayList<Pair> road_additions = new ArrayList<Pair>();

        boolean legal = true;


        while (results.hasNext()) {
            QuerySolution sol = results.nextSolution();
            System.out.println(sol);

            if (sol.get("p") != null) {
                participants.add(new Pair(Integer.parseInt(sol.get("r").asNode().getLocalName().substring(sol.get("r").asNode().getLocalName().length() - 1)), sol.get("p").asNode().getLocalName()));
            }
            if (sol.get("s") != null) {
                states.add(new Pair(Integer.parseInt(sol.get("r").asNode().getLocalName().substring(sol.get("r").asNode().getLocalName().length() - 1)), sol.get("s").asNode().getLocalName()));
            }
            if (sol.get("r") != null) {
                roads.add(new Pair(Integer.parseInt(sol.get("r").asNode().getLocalName().substring(sol.get("r").asNode().getLocalName().length() - 1)), sol.get("r").asNode().getLocalName()));
            }
            if (sol.get("a") != null) {
                road_additions.add(new Pair(Integer.parseInt(sol.get("r").asNode().getLocalName().substring(sol.get("r").asNode().getLocalName().length() - 1)), sol.get("a").asNode().getLocalName()));
            }

        }

        for (int i = 0; i < roads.size(); i++) {

            if (roads.get(i).getValue().matches("road(.*)")) {
                System.out.println("test ");
                if (states.get(i).getValue().matches("overtake")) {

                    if (road_additions.size() != 0) {

                        for (int j = 0; j < road_additions.size(); j++) {
                            if (road_additions.get(j).getKey() == roads.get(i).getKey() && states.get(i).getValue().equals("overtake") && road_additions.get(j).getValue().equals("solid_line")) {
                                legal = false;
                                log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Overtaking over solid line. Situation is not STVO conform");
                            }
                        }
                    }
                    if (legal) {

                        String query = String.join(System.lineSeparator(),
                                "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                "                                                                                      ",
                                "                SELECT ?s  ?r   ?p                                           ",
                                "                WHERE {                                                               ",
                                "                  ?r xmlns:hasParticipantOtherLane	?p .",
                                " 				   ?p xmlns:hasState ?s .                          ",
                                "}");

                        System.out.println(query);
                        QueryExecution qexe = QueryExecutionFactory.create(query, model);
                        ResultSet result = qexe.execSelect();
                        while (result.hasNext()) {
                            QuerySolution sol = result.nextSolution();
                            System.out.println(sol);

                            if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName())) {
                                legal = false;
                                log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Overtaking into oncoming traffic. Situation is not STVO conform");

                            }
                        }
                    }
                }
                if (states.get(i).getValue().matches("turn_left")) {
                    if (legal) {

                        String query = String.join(System.lineSeparator(),
                                "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                "                                                                                      ",
                                "                SELECT ?s  ?r   ?p                                           ",
                                "                WHERE {                                                               ",
                                "                  ?r xmlns:hasParticipantOtherLane	?p .",
                                " 				   ?p xmlns:hasState ?s .                          ",
                                "}");

                        System.out.println(query);
                        QueryExecution qexe = QueryExecutionFactory.create(query, model);
                        ResultSet result = qexe.execSelect();
                        while (result.hasNext()) {
                            QuerySolution sol = result.nextSolution();
                            System.out.println(sol);

                            if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName())) {
                                legal = false;
                                log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Turning left into oncoming traffic. Situation is not STVO conform");

                            }
                        }
                    }
                }

            } else if (roads.get(i).getValue().matches("cross(.*)")) {
                System.out.println("test crossing");

                if (road_additions.size() != 0) {
                    for (int j = 0; j < road_additions.size(); j++) {
                        if (road_additions.get(j).getKey() == roads.get(i).getKey() && !states.get(i).getValue().equals("stop") && road_additions.get(j).getValue().equals("stop_sign")) {
                            legal = false;
                            log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Driving over Stop sign. Situation is not STVO conform");
                            break;
                        }
                    }

                    for (int j = 0; j < road_additions.size(); j++) {
                        if (road_additions.get(j).getKey() == roads.get(i).getKey() && !states.get(i).getValue().equals("stop") && road_additions.get(j).getValue().matches("(.*)red(.*)")) {
                            legal = false;
                            log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Driving over red traffic light. Situation is not STVO conform");
                            break;
                        }
                    }
                }


                if (legal && states.get(i).getValue().matches("turn_left")) {

                    String query = String.join(System.lineSeparator(),
                            "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                            "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                            "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                            "                                                                                      ",
                            "                SELECT ?s  ?r   ?p                                           ",
                            "                WHERE {                                                               ",
                            "                  ?r xmlns:hasParticipantOtherLane	?p .",
                            " 				   ?p xmlns:hasState ?s .                          ",
                            "}");

                    System.out.println(query);
                    QueryExecution qexe = QueryExecutionFactory.create(query, model);
                    ResultSet result = qexe.execSelect();
                    while (result.hasNext()) {
                        QuerySolution sol = result.nextSolution();
                        System.out.println(sol);

                        if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName()) && (sol.get("s").asNode().getLocalName().equals("drive_straight") || sol.get("s").asNode().getLocalName().equals("turn_right"))) {
                            legal = false;
                            log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Turning left into oncoming traffic. Situation is not STVO conform");

                        }
                    }

                    if (legal) {
                        query = String.join(System.lineSeparator(),
                                "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                "                                                                                      ",
                                "                SELECT ?s  ?r   ?p                                           ",
                                "                WHERE {                                                               ",
                                "                  ?r xmlns:hasParticipantRight	?p .",
                                " 				   ?p xmlns:hasState ?s .                          ",
                                "}");

                        System.out.println(query);
                        qexe = QueryExecutionFactory.create(query, model);
                        result = qexe.execSelect();
                        while (result.hasNext()) {
                            QuerySolution sol = result.nextSolution();
                            System.out.println(sol);

                            if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName()) && (sol.get("s").asNode().getLocalName().equals("drive_straight") || sol.get("s").asNode().getLocalName().equals("turn_left"))) {
                                legal = false;
                                log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Not giving way to right hand traffic. Situation is not STVO conform");

                            }
                        }

                    }


                }

                if (legal && states.get(i).getValue().matches("drive_straight")) {

                    for (Pair road_addition : road_additions) {
                        if (road_addition.getKey() == roads.get(i).getKey() && road_addition.getValue().equals("giveway_sign")) {

                            query = String.join(System.lineSeparator(),
                                    "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                    "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                    "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                    "                                                                                      ",
                                    "                SELECT ?s  ?r   ?p                                           ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantRight	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionRight	?a }.   ",
                                    "}");

                            System.out.println(query);
                            QueryExecution qexe = QueryExecutionFactory.create(query, model);
                            ResultSet result = qexe.execSelect();
                            while (result.hasNext()) {
                                QuerySolution sol = result.nextSolution();
                                System.out.println(sol);

                                if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName())) {
                                    legal = false;
                                    log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Not giving way to right hand traffic. Situation is not STVO conform");

                                }

                            }
                            query = String.join(System.lineSeparator(),
                                    "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                    "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                    "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                    "                                                                                      ",
                                    "                SELECT ?s  ?r   ?p                                           ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantLeft	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionLeft	?a }.   ",
                                    "}");

                            System.out.println(query);
                            QueryExecution qexe1 = QueryExecutionFactory.create(query, model);
                            ResultSet result1 = qexe1.execSelect();
                            while (result1.hasNext()) {
                                QuerySolution sol = result1.nextSolution();
                                System.out.println(sol);

                                if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName())) {

                                    if (sol.get("a") == null) {
                                        legal = false;
                                        log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Not giving way to left hand traffic. Situation is not STVO conform");
                                    }
                                }

                            }

                        } else {

                            query = String.join(System.lineSeparator(),
                                    "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                    "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                    "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                    "                                                                                      ",
                                    "                SELECT ?s  ?r   ?p                                           ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantRight	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionRight	?a }.   ",
                                    "}");

                            System.out.println(query);
                            QueryExecution qexe = QueryExecutionFactory.create(query, model);
                            ResultSet result = qexe.execSelect();
                            while (result.hasNext()) {
                                QuerySolution sol = result.nextSolution();
                                System.out.println(sol);

                                if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName())) {

                                    if (sol.get("a") == null) {
                                        legal = false;
                                        log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Not giving way to right hand traffic. Situation is not STVO conform");
                                    }

                                }
                            }

                        }

                    }
                }

                if (legal && states.get(i).getValue().matches("turn_right")) {

                    for (int j = 0; j < road_additions.size(); j++) {
                        if (road_additions.get(j).getKey() == roads.get(i).getKey() && road_additions.get(j).getValue().equals("giveway_sign")) {


                            query = String.join(System.lineSeparator(),
                                    "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                    "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                    "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                    "                                                                                      ",
                                    "                SELECT ?s  ?r   ?p                                           ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantLeft	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionLeft	?a }.   ",
                                    "}");

                            System.out.println(query);
                            QueryExecution qexe = QueryExecutionFactory.create(query, model);
                            ResultSet result = qexe.execSelect();
                            while (result.hasNext()) {
                                QuerySolution sol = result.nextSolution();
                                System.out.println(sol);

                                if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName()) && (sol.get("s").asNode().getLocalName().equals("drive_straight"))) {

                                    if (sol.get("a") == null) {
                                        legal = false;
                                        log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Not giving way to left hand traffic. Situation is not STVO conform");

                                    }
                                }
                            }
                        }

                        //!!!!!! Maybe special case, where participant other lane has no giveway sign

                    }
                }

            }
        }


        if (legal) {
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