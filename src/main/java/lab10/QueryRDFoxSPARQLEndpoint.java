package lab10;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;


public class QueryRDFoxSPARQLEndpoint {
	
	public QueryRDFoxSPARQLEndpoint(String endpoint, String queryStr) {
		
		Query q = QueryFactory.create(queryStr);
	
		
		QueryExecution qe =
				QueryExecutionFactory.sparqlService(endpoint,q);
				try {
				ResultSet res = qe.execSelect();
				while( res.hasNext()) {
					QuerySolution soln = res.next();
					RDFNode f1 = soln.get("?forename1");  //Variable name in select
					RDFNode s1 = soln.get("?surname1");  //Variable name in select
					RDFNode f2 = soln.get("?forename2");  //Variable name in select
					RDFNode s2 = soln.get("?surname2");  //Variable name in select
					
					System.out.println(f1 + " " + s1 + " has Teammate " + f2 + " " + s2);
					
					
				}
			    
				} finally {
				qe.close();
				}
			
	}

	
	public static void main(String[] args) {

		//Query a remote RDF graph (e.g., SPARQL endpoint)
		//Endpoint for RDFox
		String rdfox_endpoint = "http://localhost:12110/datastores/f1/sparql";  //?datastore=f1
		
		String rdfox_query1 = ""
				+ "PREFIX : <https://rdfox.com/examples/f1/> \n"
				+ "SELECT ?forename1 ?surname1 ?forename2 ?surname2\r\n"
				+ "WHERE {\r\n"
				+ "    ?driver1 :hasTeammate ?driver2 . \r\n"
				+ "	?driver1 :driver_forename ?forename1 .  \r\n"
				+ "    ?driver1 :driver_surname ?surname1 .  \r\n"
				+ "	?driver2 :driver_forename ?forename2 .  \r\n"
				+ "    ?driver2 :driver_surname ?surname2 .\r\n"
				+ "}\r\n"
				+ "LIMIT 10";
		System.out.println("\nQuerying for Teammates.");
		new QueryRDFoxSPARQLEndpoint(rdfox_endpoint, rdfox_query1);
		
	}
	
}
