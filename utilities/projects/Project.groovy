package utilities.projects

import utilities.projects.Repo

import org.yaml.snakeyaml.Yaml
import groovy.io.FileType
import java.io.File

class Project
{
	String host = ''
	String project_key = ''
	String project_name = ''
	List branchNames = ''
	List<Repo> repos = [:]

	// constructor
	//Project(){}

	// Getter
	String getHost() { return host }

	// Setter
	void setHost(String host) { this.host = host }
}