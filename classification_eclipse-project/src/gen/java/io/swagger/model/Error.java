/*
 * Predict with a CPSign classification model
 * Service that deploys a CPSign classification model and allows for predictions to be made by the deployed model.
 *
 * OpenAPI spec version: 0.1.0
 * Contact: info@genettasoft.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.model;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Error
 */
@ApiModel(description = "Error")
public class Error {
	@JsonProperty("code")
	@ApiModelProperty(required = true, value = "HTTP status code", example="400")
	@NotNull
	private final Integer code;

	@JsonProperty("message")
	@ApiModelProperty(required = true, value = "Error message", example="Illegal parameter")
	@NotNull
	private final String message;

	public Error(int code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Error error = (Error) o;
		return Objects.equals(this.code, error.code) &&
				Objects.equals(this.message, error.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, message);
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(){
		JSONObject jsonResponse = new JSONObject();

		jsonResponse.put("code", code);
		jsonResponse.put("message", message);

		return jsonResponse;
	}
	
	@Override
	public String toString() {
		return toJSON().toJSONString();
	}

}

