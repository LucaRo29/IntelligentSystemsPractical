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

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//Luca Rodiga 1620972
//Andreas Schriebl 11703008
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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

        //log.info(query);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        model.read(document);


        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet results = qexec.execSelect();

        ArrayList<Pair> participants = new ArrayList<Pair>();
        ArrayList<Pair> states = new ArrayList<Pair>();
        ArrayList<Pair> roads = new ArrayList<Pair>();
        ArrayList<Pair> road_additions = new ArrayList<Pair>();

        boolean legal = true;
        boolean wholeworldlegal = true;


        while (results.hasNext()) {
            QuerySolution sol = results.nextSolution();
            // System.out.println(sol);

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
                //System.out.println("test ");
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

                        //System.out.println(query);
                        QueryExecution qexe = QueryExecutionFactory.create(query, model);
                        ResultSet result = qexe.execSelect();
                        while (result.hasNext()) {
                            QuerySolution sol = result.nextSolution();
                            //System.out.println(sol);

                            if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName())) {
                                legal = false;
                                log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Overtaking into oncoming traffic. Situation is not STVO conform");

                            }
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
                            "                  ?r xmlns:hasParticipantSidewalk	?p .",
                            " 				   ?p xmlns:hasState ?s .                          ",
                            "}");

                    //System.out.println(query);
                    QueryExecution qexe = QueryExecutionFactory.create(query, model);
                    ResultSet result = qexe.execSelect();
                    while (result.hasNext()) {
                        QuerySolution sol = result.nextSolution();
                        //System.out.println(sol);

                        if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName())) {

                            if (road_additions.size() != 0) {
                                for (int j = 0; j < road_additions.size(); j++) {
                                    if (road_additions.get(j).getKey() == roads.get(i).getKey() && !states.get(i).getValue().equals("stop") && road_additions.get(j).getValue().matches("(.*)crosswalk(.*)")) {

                                        if (sol.get("s").asNode().getLocalName().equals("cross_street"))
                                            legal = false;
                                        log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Not stopping for pedestrian at crosswalk. Situation is not STVO conform");
                                        break;
                                    }
                                }
                            }


                        }
                    }

                }
                if (!legal) {
                    wholeworldlegal = false;
                }

            } else if (roads.get(i).getValue().matches("cross(.*)")) {
                legal = true;
                // System.out.println("test crossing");

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
                            "                SELECT *                                          ",
                            "                WHERE {                                                               ",
                            "                  ?r xmlns:hasParticipantOtherLane	?p .",
                            " 				   ?p xmlns:hasState ?s .                          ",
                            " 				 OPTIONAL {  ?r xmlns:hasRoadAddition	?a }.                           ",
                            " 				 OPTIONAL {  ?r xmlns:hasRoadAdditionOpposite	?o }.                           ",
                            "}");

                    //System.out.println(query);
                    QueryExecution qexe = QueryExecutionFactory.create(query, model);
                    ResultSet result = qexe.execSelect();
                    while (result.hasNext()) {
                        QuerySolution sol = result.nextSolution();
                        //System.out.println(sol);

                        if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName()) && (sol.get("s").asNode().getLocalName().equals("drive_straight") || sol.get("s").asNode().getLocalName().equals("turn_right"))) {

                            if (sol.get("o") == null || !(sol.get("o").asNode().getLocalName().equals("stop_sign") || sol.get("o").asNode().getLocalName().equals("giveway_sign"))
                                    || (sol.get("a") != null && sol.get("a").asNode().getLocalName().equals("giveway_sign") && sol.get("o").asNode().getLocalName().equals("giveway_sign"))) {
                                //System.out.println(sol);


                                legal = false;
                                log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Turning left into oncoming traffic. Situation is not STVO conform");
                            }
                        }


                    }


                    if (legal) {
                        query = String.join(System.lineSeparator(),
                                "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                "                                                                                      ",
                                "                SELECT *                                         ",
                                "                WHERE {                                                               ",
                                "                  ?r xmlns:hasParticipantRight	?p .",
                                " 				   ?p xmlns:hasState ?s .                          ",
                                " 				 OPTIONAL {  ?r xmlns:hasRoadAddition	?a }.                           ",
                                " 				 OPTIONAL {  ?r xmlns:hasRoadAdditionRight	?o }.                           ",
                                "}");

                        //System.out.println(query);
                        qexe = QueryExecutionFactory.create(query, model);
                        result = qexe.execSelect();

                        while (result.hasNext()) {
                            QuerySolution sol = result.nextSolution();
                            //System.out.println(sol);

                            if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName()) && (sol.get("s").asNode().getLocalName().equals("drive_straight") || sol.get("s").asNode().getLocalName().equals("turn_left"))) {

                                if (sol.get("o") == null || !(sol.get("o").asNode().getLocalName().equals("stop_sign") || sol.get("o").asNode().getLocalName().equals("giveway_sign"))
                                        || (sol.get("a") != null && sol.get("a").asNode().getLocalName().equals("giveway_sign") && sol.get("o").asNode().getLocalName().equals("giveway_sign"))) {
                                    legal = false;
                                    log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Not giving way to right hand traffic. Situation is not STVO conform");
                                }

                            }
                        }

                    }
                    if (legal) {
                        query = String.join(System.lineSeparator(),
                                "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                "                                                                                      ",
                                "                SELECT *                                         ",
                                "                WHERE {                                                               ",
                                "                  ?r xmlns:hasParticipantLeft	?p .",
                                " 				   ?p xmlns:hasState ?s .                          ",
                                " 				 OPTIONAL {  ?r xmlns:hasRoadAddition	?a }.                           ",
                                " 				 OPTIONAL {  ?r xmlns:hasRoadAdditionLeft	?o }.                           ",
                                "}");


                        //System.out.println(query);
                        qexe = QueryExecutionFactory.create(query, model);
                        result = qexe.execSelect();

                        while (result.hasNext()) {
                            QuerySolution sol = result.nextSolution();
                            //System.out.println(sol);

                            if (sol.get("o") != null && sol.get("s").asNode().getLocalName().equals("cross_street") && sol.get("o").asNode().getLocalName().equals("pedestrian_crosswalk")) {
                                legal = false;
                                log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Running over pedestrian. Situation is not STVO conform");
                            }


                            if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName()) && (sol.get("s").asNode().getLocalName().equals("drive_straight") || sol.get("s").asNode().getLocalName().equals("turn_left"))) {
                                if (sol.get("a") != null && sol.get("a").asNode().getLocalName().equals("giveway_sign") && sol.get("o") == null) {
                                    legal = false;
                                    log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Not giving way to left hand traffic despite traffic sign. Situation is not STVO conform");
                                }

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
                                    "                SELECT *                                          ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantOtherLane	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionOpposite ?a }.   ",
                                    "}");

                            //System.out.println(query);
                            QueryExecution qexe = QueryExecutionFactory.create(query, model);
                            ResultSet result = qexe.execSelect();
                            while (result.hasNext()) {
                                QuerySolution sol = result.nextSolution();
                                //System.out.println(sol);


                                if (sol.get("o") != null && sol.get("s").asNode().getLocalName().equals("cross_street") && sol.get("o").asNode().getLocalName().equals("pedestrian_crosswalk")) {
                                    legal = false;
                                    log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Running over pedestrian. Situation is not STVO conform");
                                }

                                if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName()) && sol.get("s").asNode().getLocalName().equals("turn_left")) {
                                    if (!(sol.get("a") != null && (sol.get("a").asNode().getLocalName().equals("stop_sign") || sol.get("a").asNode().getLocalName().equals("giveway_sign")))) {
                                        legal = false;
                                        log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Not giving way to ongoing traffic. Situation is not STVO conform");
                                    }

                                }

                            }


                            query = String.join(System.lineSeparator(),
                                    "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                    "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                    "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                    "                                                                                      ",
                                    "                SELECT *                                          ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantRight	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionRight	?a }.   ",
                                    "}");

                            //System.out.println(query);
                            qexe = QueryExecutionFactory.create(query, model);
                            result = qexe.execSelect();
                            while (result.hasNext()) {
                                QuerySolution sol = result.nextSolution();
                                //System.out.println(sol);

                                if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName()) && !(sol.get("s").asNode().getLocalName().equals("stop"))) {
                                    if (!(sol.get("a") != null && sol.get("a").asNode().getLocalName().equals("stop_sign"))) {
                                        legal = false;
                                        log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Not giving way to right hand traffic. Situation is not STVO conform");
                                    }

                                }

                            }
                            query = String.join(System.lineSeparator(),
                                    "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                    "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                    "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                    "                                                                                      ",
                                    "                SELECT *                                           ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantLeft	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionLeft	?a }.   ",
                                    "}");

                            //System.out.println(query);
                            QueryExecution qexe1 = QueryExecutionFactory.create(query, model);
                            ResultSet result1 = qexe1.execSelect();
                            while (result1.hasNext()) {
                                QuerySolution sol = result1.nextSolution();
                                //System.out.println(sol);

                                if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName()) && !(sol.get("s").asNode().getLocalName().equals("stop"))) {

                                    if (!(sol.get("a") != null && sol.get("a").asNode().getLocalName().equals("stop_sign"))) {
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
                                    "                SELECT *                                          ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantOtherLane	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionOpposite ?a }.   ",
                                    "}");

                            //System.out.println(query);
                            QueryExecution qexe = QueryExecutionFactory.create(query, model);
                            ResultSet result = qexe.execSelect();
                            while (result.hasNext()) {
                                QuerySolution sol = result.nextSolution();
                                //System.out.println(sol);

                                if (sol.get("o") != null && sol.get("s").asNode().getLocalName().equals("cross_street") && sol.get("o").asNode().getLocalName().equals("pedestrian_crosswalk")) {
                                    legal = false;
                                    log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Running over pedestrian. Situation is not STVO conform");
                                }

                            }

                            query = String.join(System.lineSeparator(),
                                    "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                    "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                    "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                    "                                                                                      ",
                                    "                SELECT *                                          ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantRight	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionRight	?a }.   ",
                                    "}");

                            //System.out.println(query);
                            qexe = QueryExecutionFactory.create(query, model);
                            result = qexe.execSelect();

                            while (result.hasNext()) {
                                QuerySolution sol = result.nextSolution();
                                //System.out.println(sol);

                                if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName()) && !(sol.get("s").asNode().getLocalName().equals("stop"))) {

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
                                    "                SELECT *                                          ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantRight	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionRight ?a }.   ",
                                    "}");

                            //System.out.println(query);
                            QueryExecution qexe = QueryExecutionFactory.create(query, model);
                            ResultSet result = qexe.execSelect();
                            while (result.hasNext()) {
                                QuerySolution sol = result.nextSolution();
                                //System.out.println(sol);

                                if (sol.get("o") != null && sol.get("s").asNode().getLocalName().equals("cross_street") && sol.get("o").asNode().getLocalName().equals("pedestrian_crosswalk")) {
                                    legal = false;
                                    log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Running over pedestrian. Situation is not STVO conform");
                                }

                            }


                            query = String.join(System.lineSeparator(),
                                    "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                    "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                    "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                    "                                                                                      ",
                                    "                SELECT *                                          ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantOtherLane	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionOpposite ?a }.   ",
                                    "}");

                            //System.out.println(query);
                            qexe = QueryExecutionFactory.create(query, model);
                            result = qexe.execSelect();

                            while (result.hasNext()) {
                                QuerySolution sol = result.nextSolution();
                                //System.out.println(sol);

                                if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName()) && sol.get("s").asNode().getLocalName().equals("turn_left")) {
                                    if (!(sol.get("a") != null && (sol.get("a").asNode().getLocalName().equals("stop_sign") || sol.get("a").asNode().getLocalName().equals("giveway_sign")))) {
                                        legal = false;
                                        log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Not giving way to ongoing traffic. Situation is not STVO conform");
                                    }

                                }

                            }


                            query = String.join(System.lineSeparator(),
                                    "                PREFIX xmlns:     <http://www.semanticweb.org/andi/ontologies/2021/6/practical_new#>",
                                    "                PREFIX owl:       <http://www.w3.org/2002/07/owl#>",
                                    "                PREFIX rdfs:      <http://www.w3.org/2000/01/rdf-schema#>",
                                    "                                                                                      ",
                                    "                SELECT *                                           ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantLeft	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionLeft	?a }.   ",
                                    "}");

                            //System.out.println(query);
                            qexe = QueryExecutionFactory.create(query, model);
                            result = qexe.execSelect();
                            while (result.hasNext()) {
                                QuerySolution sol = result.nextSolution();
                                //System.out.println(sol);

                                if (roads.get(i).getValue().equals(sol.get("r").asNode().getLocalName()) && (sol.get("s").asNode().getLocalName().equals("drive_straight"))) {

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
                                    "                SELECT *                                          ",
                                    "                WHERE {                                                               ",
                                    "                  ?r xmlns:hasParticipantRight	?p .",
                                    " 				   ?p xmlns:hasState ?s .                          ",
                                    "                 OPTIONAL {  ?r xmlns:hasRoadAdditionRight ?a }.   ",
                                    "}");

                            //System.out.println(query);
                            QueryExecution qexe = QueryExecutionFactory.create(query, model);
                            ResultSet result = qexe.execSelect();
                            while (result.hasNext()) {
                                QuerySolution sol = result.nextSolution();
                                //System.out.println(sol);

                                if (sol.get("o") != null && sol.get("s").asNode().getLocalName().equals("cross_street") && sol.get("o").asNode().getLocalName().equals("pedestrian_crosswalk")) {
                                    legal = false;
                                    log.info(roads.get(i).getValue() + " " + participants.get(i).getValue() + ": Running over pedestrian. Situation is not STVO conform");
                                }

                            }
                        }


                    }
                }
                if (!legal) {
                    wholeworldlegal = false;
                }

            }
        }


        if (legal && wholeworldlegal) {
            log.info("Situation is STVO conform");
        }


    }
}