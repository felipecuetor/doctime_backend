package stage3.web;

import javax.ws.rs.GET;
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
@Path("/timeline")
public class TimelineData {
	private String last_query = "";

	@GET
	@Path("/space/{input1}")
	@Produces("application/json")
	public String ping(@PathParam("input1") String input1) {
		return querySPARQL(23 + Integer.parseInt(input1));
	}

	public String querySPARQL(int currentTriple) {
		Repository rep = new SPARQLRepository("http://172.24.101.57/blazegraph/namespace/kb/sparql");
		rep.init();
		try (RepositoryConnection con = rep.getConnection()) {
			String queryString = "";
			queryString += "PREFIX dtont: <http://172.24.101.57/ontology_description#> ";
			queryString += "SELECT DISTINCT ?context_subject ?date (COUNT(distinct ?context_url) as ?total_links) (COUNT(distinct ?document) as ?total_documents) ";
			queryString += "WHERE{?document dtont:publishingDate ?date.?document dtont:propogatedBy ?context.?context dtont:subject ?context_subject.?document dtont:url ?context_url} ";
			queryString += "GROUP BY ?context_subject ?date ORDER BY ?date LIMIT 25 OFFSET "
					+ currentTriple;

			TupleQuery graphQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			last_query = queryString;
			TupleQueryResult timeline_list = graphQuery.evaluate();
			String resp = "{\"list\":[";
			int current_elem = 1;
			while (timeline_list.hasNext()) {

				BindingSet currentEvent = timeline_list.next();
				String subject = currentEvent.getValue("context_subject").toString();
				String date = currentEvent.getValue("date").toString();
				String total_links = currentEvent.getValue("total_links").toString();
				String total_documents = currentEvent.getValue("total_documents").toString();

				String event_json = "{\"date\":\"" + Simple_data_csv_adapter.json_compatible(date) + "\",";
				event_json += "\"context_subject\":\"" + Simple_data_csv_adapter.json_compatible(subject) + "\",";
				event_json += "\"total_links\":\"" + Simple_data_csv_adapter.json_compatible(total_links) + "\",";
				event_json += "\"total_documents\":\"" + Simple_data_csv_adapter.json_compatible(total_documents) + "\"}";
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
