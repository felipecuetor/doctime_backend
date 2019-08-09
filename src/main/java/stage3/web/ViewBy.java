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
@Path("/view_by")
public class ViewBy {

	
	@GET
	@Path("/class/{input1}/{input2}")
	@Produces("application/json")
	public String querySPARQL(@PathParam("input1") String input1, @PathParam("input2") String input2) {
		
		
		Repository rep = new SPARQLRepository("http://172.24.101.57/blazegraph/namespace/kb/sparql");
		rep.init();
		try (RepositoryConnection con = rep.getConnection()) {
			String queryString = "";
			queryString += "PREFIX dtont: <http://172.24.101.57/ontology_description#> ";
			queryString += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
			queryString += "SELECT ?element ";
			queryString += "WHERE{?element rdf:type dtont:"+input1+"} ";
			queryString += "ORDER BY ?element LIMIT 15 OFFSET "+input2;

			TupleQuery graphQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult timeline_list = graphQuery.evaluate();
			String resp = "{\"list\":[";
			int current_elem = 1;
			while (timeline_list.hasNext()) {

				BindingSet currentEvent = timeline_list.next();
				String element = currentEvent.getValue("element").toString();

				String event_json = "{\"element\":\"" + Simple_data_csv_adapter.json_compatible(element) +"\"}";
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
	
	
	@GET
	@Path("/property/{input1}/{input2}")
	@Produces("application/json")
	public String querySPARQLProperty(@PathParam("input1") String input1, @PathParam("input2") String input2) {
		
		
		Repository rep = new SPARQLRepository("http://172.24.101.57/blazegraph/namespace/kb/sparql");
		rep.init();
		try (RepositoryConnection con = rep.getConnection()) {
			String queryString = "";
			queryString += "PREFIX dtont: <http://172.24.101.57/ontology_description#> ";
			queryString += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
			queryString += "SELECT ?element ?element2 ";
			queryString += "WHERE{?element dtont:"+input1+" ?element2} ";
			queryString += "ORDER BY ?element LIMIT 15 OFFSET "+input2;

			TupleQuery graphQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult timeline_list = graphQuery.evaluate();
			String resp = "{\"list\":[";
			int current_elem = 1;
			while (timeline_list.hasNext()) {

				BindingSet currentEvent = timeline_list.next();
				String element = currentEvent.getValue("element").toString();
				String element2 = currentEvent.getValue("element2").toString();

				String event_json = "{\"element\":\"" + Simple_data_csv_adapter.json_compatible(element)+"\",";
				event_json+="\"element2\":\""+Simple_data_csv_adapter.json_compatible(element2)+"\"}";
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
			queryString += "SELECT ?element ?element2 ";
			queryString += "WHERE {{?element ?element2 <"+base_iri+">.} ";
			queryString += "UNION {<"+base_iri+"> ?element  ?element2.}} ";
			queryString += "LIMIT 15 OFFSET "+input1;

			TupleQuery graphQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult timeline_list = graphQuery.evaluate();
			String resp = "{\"list\":[";
			int current_elem = 1;
			while (timeline_list.hasNext()) {

				BindingSet currentEvent = timeline_list.next();
				String element = currentEvent.getValue("element").toString();
				String element2 = currentEvent.getValue("element2").toString();

				String event_json = "{\"element\":\"" + Simple_data_csv_adapter.json_compatible(element)+"\",";
				event_json+="\"element2\":\""+Simple_data_csv_adapter.json_compatible(element2)+"\"}";
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
