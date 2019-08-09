package stage3.web;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import utility.Simple_data_csv_adapter;

//To test:
//http://localhost:8080/WebSemantica_Proyecto/webapp/ping/
@Path("/autocomplete")
public class CompleteText {

	
	//Retorna todas las clases de la ontologia
	@GET
	@Path("/class/")
	@Produces("application/json")
	public String querySPARQL() {
		Repository rep = new SPARQLRepository("http://172.24.101.57/blazegraph/namespace/kb/sparql");
		rep.init();
		try (RepositoryConnection con = rep.getConnection()) {
			String queryString = "";
			queryString += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
			queryString += "SELECT ?class ";
			queryString += "WHERE{?class rdf:type rdfs:Class}";

			TupleQuery graphQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult timeline_list = graphQuery.evaluate();
			String resp = "{\"list\":[";
			int current_elem = 1;
			while (timeline_list.hasNext()) {
				BindingSet currentEvent = timeline_list.next();
				String class_string = currentEvent.getValue("class").toString();
				String event_json = "{\"class\":\"" + Simple_data_csv_adapter.json_compatible(class_string) + "\"}";
				resp += event_json;
				if (timeline_list.hasNext()) {
					resp += ",";
					current_elem++;
				}
				
			}

			resp += "]}";
			System.out.println(resp);
			return resp;
		}
	}
	
	
	
	@GET
	@Path("/property/")
	@Produces("application/json")
	public String querySPARQLProperty() {
		Repository rep = new SPARQLRepository("http://172.24.101.57/blazegraph/namespace/kb/sparql");
		rep.init();
		try (RepositoryConnection con = rep.getConnection()) {
			String queryString = "";
			queryString += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
			queryString += "SELECT ?class ";
			queryString += "WHERE{?class rdf:type rdf:Property}";

			TupleQuery graphQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult timeline_list = graphQuery.evaluate();
			String resp = "{\"list\":[";
			int current_elem = 1;
			while (timeline_list.hasNext()) {
				BindingSet currentEvent = timeline_list.next();
				String class_string = currentEvent.getValue("class").toString();
				String event_json = "{\"class\":\"" + Simple_data_csv_adapter.json_compatible(class_string) + "\"}";
				resp += event_json;
				if (timeline_list.hasNext()) {
					resp += ",";
					current_elem++;
				}
				
			}

			resp += "]}";
			System.out.println(queryString);
			return resp;
		}
	}
	
	@GET
	@Path("/instance/{input1}")
	@Produces("application/json")
	public String querySPARQLInstance(@PathParam("input1") String input1, @HeaderParam("base-iri") String base_iri) {
		Repository rep = new SPARQLRepository("http://172.24.101.57/blazegraph/namespace/kb/sparql");
		rep.init();
		try (RepositoryConnection con = rep.getConnection()) {
			String queryString = "";
			queryString += "PREFIX dtont: <http://172.24.101.57/ontology_description#> ";
			queryString += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
			queryString += "SELECT ?element  ?element2 ?element3 ";
			queryString += "WHERE {{?element ?element2 ?element3.FILTER( STRSTARTS(STR(?element),\""+base_iri+"\") ).} ";
			queryString += "UNION {?element3 ?element  ?element2.FILTER( STRSTARTS(STR(?element),\""+base_iri+"\") ).} ";
			queryString += "UNION {?element2 ?element3 ?element.FILTER( STRSTARTS(STR(?element),\""+base_iri+"\") ).}}  ";
			queryString += "LIMIT 15 OFFSET "+input1;

			TupleQuery graphQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult timeline_list = graphQuery.evaluate();
			String resp = "{\"list\":[";
			int current_elem = 1;
			while (timeline_list.hasNext()) {
				BindingSet currentEvent = timeline_list.next();
				String element = currentEvent.getValue("element").toString();
				String element2 = currentEvent.getValue("element2").toString();
				String element3 = currentEvent.getValue("element3").toString();
				String event_json = "{\"element\":\"" + Simple_data_csv_adapter.json_compatible(element)+"\",";
				event_json+="\"element2\":\""+Simple_data_csv_adapter.json_compatible(element2)+"\",";
				event_json+="\"element3\":\""+Simple_data_csv_adapter.json_compatible(element3)+"\"}";
				resp += event_json;
				if (timeline_list.hasNext()) {
					resp += ",";
					current_elem++;
				}
				
			}

			resp += "]}";
			return resp;
		}
	}
}
