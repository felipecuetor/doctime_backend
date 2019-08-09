package stage3.web;

import java.util.HashMap;

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
@Path("/topic")
public class TopicGraph {

	// Retorna todas las clases de la ontologia
	@GET
	@Path("/info/")
	@Produces("application/json")
	public String querySPARQL() {
		HashMap topic_map = new HashMap();
		Repository rep = new SPARQLRepository("http://172.24.101.57/blazegraph/namespace/kb/sparql");
		rep.init();
		try (RepositoryConnection con = rep.getConnection()) {

			String queryString2 = "";
			queryString2 += "PREFIX dtont: <http://172.24.101.57/ontology_description#> ";
			queryString2 += "SELECT ?topic (COUNT (?document) as ?total) ";
			queryString2 += "WHERE{ ?document dtont:titleReferencesTopic ?topic.}";
			queryString2 += "Group BY ?topic order by DESC(?total) LIMIT 100";

			TupleQuery graphQuery2 = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString2);
			TupleQueryResult timeline_list2 = graphQuery2.evaluate();
			String resp2 = "\"nodes\":[";
			int current_elem2 = 1;
			while (timeline_list2.hasNext()) {

				BindingSet currentEvent = timeline_list2.next();
				String class_string = currentEvent.getValue("topic").toString();
				String amountString = currentEvent.getValue("total").toString();
				int nodeWeight = (int) Math.ceil((double) (Integer.parseInt(Simple_data_csv_adapter.json_compatible(amountString).split("'")[1])/100));
				topic_map.put(class_string, current_elem2+"");
				String event_json = "{\"id\":" + current_elem2 + "," + "\"name\":\""
						+ Simple_data_csv_adapter.json_compatible(class_string) + "\",";
				event_json += "\"value\":\"" + nodeWeight + "\",";
				event_json += "\"group\":\"" + 1 + "\"}";
				resp2 += event_json;
				if (timeline_list2.hasNext()) {
					resp2 += ",";
					current_elem2++;
				}

			}
			resp2 += "]";
			System.out.println(resp2);

			String queryString = "";
			queryString += "PREFIX dtont: <http://172.24.101.57/ontology_description#> ";
			queryString += "SELECT ?topic ?topic2 (COUNT (?document) as ?total) ";
			queryString += "WHERE{ ?document dtont:titleReferencesTopic ?topic. ?document dtont:titleReferencesTopic ?topic2. FILTER(?topic!=?topic2).}";
			queryString += "Group BY ?topic ?topic2 order by DESC(?total) LIMIT 500";

			TupleQuery graphQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult timeline_list = graphQuery.evaluate();
			String resp = "\"links\":[";
			int current_elem = 1;
			while (timeline_list.hasNext()) {
				BindingSet currentEvent = timeline_list.next();
				String class_string = currentEvent.getValue("topic").toString();
				String class2_string = currentEvent.getValue("topic2").toString();
				String topic_id = (String) topic_map.get(class_string);
				String topic2_id = (String) topic_map.get(class2_string);
				if (topic_id != null && topic2_id != null) {
					String event_json = "{\"source\":\"" + Simple_data_csv_adapter.json_compatible(topic_id + "")
							+ "\"," + "\"target\":\"" + Simple_data_csv_adapter.json_compatible(topic2_id + "") + "\"}";
					resp += event_json;
					if (timeline_list.hasNext()) {
						resp += ",";
						current_elem++;
					}
				}

			}
			if(resp.endsWith(",")) {
				resp = resp.substring(0, resp.length() - 1);
			}
			resp += "]";

			String graphData = "";
			return "{" + resp2 + "," + resp + "}";
		}
	}
}
