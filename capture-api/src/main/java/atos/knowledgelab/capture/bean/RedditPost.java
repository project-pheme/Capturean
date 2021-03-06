/*******************************************************************************
 * 	Copyright (C) 2017  ATOS Spain S.A.
 *
 * 	This file is part of the Capturean software.
 *
 * 	This program is dual licensed under the terms of GNU Affero General
 * 	Public License and proprietary for commercial usage.
 *
 *
 * 	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU Affero General Public License as
 * 	published by the Free Software Foundation, either version 3 of the
 * 	License, or (at your option) any later version.
 *
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 *
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * 	You can be released from the requirements of the license by purchasing
 * 	a commercial license or negotiating an agreement with Atos Spain S.A.
 * 	Buying such a license is mandatory as soon as you develop commercial
 * 	activities involving the Capturean software without disclosing the source 
 * 	code of your own applications. 
 *
 * 	
 * Contributors:
 *      Mateusz Radzimski (ATOS, ARI, Knowledge Lab)
 *      Iván Martínez Rodriguez (ATOS, ARI, Knowledge Lab)
 *      María Angeles Sanguino Gonzalez (ATOS, ARI, Knowledge Lab)
 *      Jose María Fuentes López (ATOS, ARI, Knowledge Lab)
 *      Jorge Montero Gómez (ATOS, ARI, Knowledge Lab)
 *      Ana Luiza Pontual Costa E Silva (ATOS, ARI, Knowledge Lab)
 *      Miguel Angel Tinte García (ATOS, ARI, Knowledge Lab)
 *      
 *******************************************************************************/
package atos.knowledgelab.capture.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RedditPost implements Serializable {

	@JsonProperty("text")
	String text;
	
	//@JsonProperty("title")
	//String title;
	
	@JsonProperty("user_screen_name")
	String userScreenName;
	
	@JsonProperty("id_str")
	String id;
	
	@JsonProperty("source_type")
	String sourceType;
	
	@JsonProperty("created_at")
	String createdAt;
	
	@JsonProperty("raw_json")
	Object rawJson;
	
	@JsonProperty("dc_id")
	Object dcId;
	
	@JsonProperty("source_url")
	String sourceUrl; 
	
	public Object getDcId() {
		return dcId;
	}
	public void setDcId(Object dcId) {
		this.dcId = dcId;
	}
//	public String getTitle() {
//		return title;
//	}
//	public void setTitle(String title) {
//		this.title = title;
//	}
	public Object getRawJson() {
		return rawJson;
	}
	public void setRawJson(Object rawJson) {
		this.rawJson = rawJson;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUserScreenName() {
		return userScreenName;
	}
	public void setUserScreenName(String userScreenName) {
		this.userScreenName = userScreenName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	

}
