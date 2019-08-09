package stage3.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

//To test:
//http://localhost:8080/WebSemantica_Proyecto/webapp/ping/
@Path("/doctime")
public class TimelineData {
	@GET
	@Path("/space/{input1}")
	@Produces("application/json")
	public String ping(@PathParam("input1") String input1) {
		TupleQueryResult timeline_list = querySPARQL(135+Integer.parseInt(input1));
		String resp = "{\"list\":[";
		while(timeline_list.hasNext()) {
			BindingSet currentEvent = timeline_list.next();
			String subject = currentEvent.getValue("context_subject").toString();
			String url = currentEvent.getValue("context_url").toString();
			String date = currentEvent.getValue("date").toString();
			String total_documents = currentEvent.getValue("total").toString();
			
			String event_json = "{\"date\"";
		}
		
		resp += "}";
		
		return "{\"ping\":\"Ping_WebSemantica\"}";
	}

	public TupleQueryResult querySPARQL(int currentTriple) {
		Repository rep = new SPARQLRepository("http://172.24.101.57/blazegraph/namespace/kb/sparql");
		rep.init();
		try (RepositoryConnection con = rep.getConnection()) {
			String queryString = "";
			queryString += "PREFIX dtont: <http://172.24.101.57/ontology_description#>";
			queryString += "SELECT ?context_subject ?context_url ?date (COUNT(distinct ?document) as ?total)";
			queryString += "WHERE{?document dtont:publishingDate ?date.?document dtont:propogatedBy ?context.?context dtont:subject ?context_subject.?context dtont:url ?context_url.}";
			queryString += "GROUP BY ?context_subject ?context_url ?date ORDER BY ?date LIMIT 100 OFFSET " + currentTriple;
			TupleQuery graphQuery = con.prepareTupleQuery(queryString);
			// con.prepareGraphQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult resp = graphQuery.evaluate();
			return resp;
		}
	}
}
