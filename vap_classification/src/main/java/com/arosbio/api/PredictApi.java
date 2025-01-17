package com.arosbio.api;

import org.slf4j.Logger;

import com.arosbio.api.model.BadRequestError;
import com.arosbio.api.model.ErrorResponse;
import com.arosbio.api.model.ModelInfo;
import com.arosbio.api.model.PredictionResult;
import com.arosbio.api.model.ServiceRunning;
import com.arosbio.impl.Predict;
import com.arosbio.services.utils.Utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/v2")
public class PredictApi  {
	
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PredictApi.class);
	private static final String PREDICT_TAG = "Predict";
	private static final String INFO_TAG = "Server info";

	@Path("/modelInfo")
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	@Operation(
			summary="Get information about this model",
			tags = { INFO_TAG },
			responses = {
					@ApiResponse(responseCode="200", description="Model info", content = @Content(
							schema = @Schema(implementation=ModelInfo.class))),
					@ApiResponse(responseCode = "503", description = "Service not available", content = @Content(
							schema = @Schema(implementation = ErrorResponse.class)))
	})
	public Response modelInfo() {
		try {
			return Predict.getModelInfo();
		} catch (Exception e) {
			return convertToErrorResponse(e);
		}
	}
	
	
	@Path("/health")
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	@Operation(
			summary="Get the status of the prediction service",
			tags = { INFO_TAG },
			responses = {
					@ApiResponse(responseCode="200", description="Service is running", 
							content = @Content(schema=@Schema(implementation = ServiceRunning.class))),
					@ApiResponse(responseCode="503", description="Service down",
							content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	public Response health() {
		try {
			return Predict.checkHealth();
		} catch (Exception e) {
			return convertToErrorResponse(e);
		}
	}

	@Path("/predict")
	@GET
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces( MediaType.APPLICATION_JSON )
	@Operation(
			summary = "Make a prediction on a given molecule", 
			tags = {PREDICT_TAG},
			description = "Predict a given molecule in SMILES or MDL v2000/v3000 format. In case a MDL is sent, it must be properly URL-encoded in UTF-8. You can use for instance https://www.urlencoder.org/ to encode your file.", 
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK", content = @Content(
							schema = @Schema(implementation = PredictionResult.class))),

					@ApiResponse(responseCode = "400", description = "Bad Request",content = @Content(
							schema = @Schema(implementation = BadRequestError.class))),

					@ApiResponse(responseCode = "500", description = "Prediction error", content = @Content(
							schema = @Schema(implementation = ErrorResponse.class))),

					@ApiResponse(responseCode = "503", description = "Service not available", content = @Content(
							schema = @Schema(implementation = ErrorResponse.class)))
			}
			)
	public Response predictGet(

			@Parameter(description = "Compound structure notation using SMILES or MDL format", required=true, example="CCCCC=O")
			@QueryParam("molecule") 
			String molecule,

			@Context SecurityContext securityContext) {
		try {
			return Predict.doPredict(molecule);
		} catch (Exception e) {
			return convertToErrorResponse(e);
		}
	}

	@Path("/predict")
	@POST
	@Consumes( MediaType.TEXT_PLAIN )
	@Produces( MediaType.APPLICATION_JSON )
	@Operation(
			summary = "Make a prediction on a given molecule", 
			tags = {PREDICT_TAG},
			description = "Predict a given molecule in SMILES or MDL v2000/v3000 format. In case a MDL is sent, it must be properly URL-encoded in UTF-8. You can use for instance https://www.urlencoder.org/ to encode your file.", 
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK", content = @Content(
							schema = @Schema(implementation = PredictionResult.class))),

					@ApiResponse(responseCode = "400", description = "Bad Request",content = @Content(
							schema = @Schema(implementation = BadRequestError.class))),

					@ApiResponse(responseCode = "500", description = "Prediction error", content = @Content(
							schema = @Schema(implementation = ErrorResponse.class))),

					@ApiResponse(responseCode = "503", description = "Service not available", content = @Content(
							schema = @Schema(implementation = ErrorResponse.class)))
			}
			)
	public Response predictPost(

			@RequestBody(description="Molecule to predict in SMILES or MDL format",
			content = @Content(examples=@ExampleObject(value="CCCCC=O"), mediaType = MediaType.TEXT_PLAIN))
			String molecule,

			@Context SecurityContext securityContext) {
		try {
			return Predict.doPredict(molecule);
		} catch (Exception e) {
			return convertToErrorResponse(e);
		}
	}

	@Path("/predictImage")
	@GET
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces({ Utils.PNG_MEDIA_TYPE, MediaType.APPLICATION_JSON }) 
	@Operation(
			summary = "Make a prediction image for the given molecule",
			tags = {PREDICT_TAG},
			description = "Predict a given molecule to get a prediction image, accepts SMILES or MDL v2000/v3000 format. In case a MDL is sent, it must be properly URL-encoded in UTF-8. You can use for instance https://www.urlencoder.org/ to encode your file.",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK", 
							content=@Content(mediaType = Utils.PNG_MEDIA_TYPE)),

					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(
							schema = @Schema(implementation = BadRequestError.class), mediaType = MediaType.APPLICATION_JSON)),

					@ApiResponse(responseCode = "500", description = "Prediction error", content = @Content(
							schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON)),

					@ApiResponse(responseCode = "503", description = "Service not available", content = @Content(
							schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON)) 
			}
			)
	public Response predictImageGet( 

			@Parameter(description = "Compound structure notation using SMILES or MDL format", 
			required=false, 
			example="CCCCC=O")
			@QueryParam("molecule") 
			String molecule,

			@Parameter(description = "Image width in pixels",
				schema = @Schema(maximum=""+Utils.MAX_IMAGE_SIZE, minimum=""+Utils.MIN_IMAGE_SIZE, defaultValue=""+Utils.DEFAULT_IMAGE_WH))
			@DefaultValue(""+Utils.DEFAULT_IMAGE_WH) 
			@QueryParam("imageWidth")
			int imageWidth,

			@Parameter(description = "Image height in pixels",
				schema = @Schema(maximum=""+Utils.MAX_IMAGE_SIZE, minimum=""+Utils.MIN_IMAGE_SIZE, defaultValue=""+Utils.DEFAULT_IMAGE_WH)) 
			@DefaultValue(""+Utils.DEFAULT_IMAGE_WH) 
			@QueryParam("imageHeight") 
			int imageHeight,

			@Parameter(description = "Add the prediction (i.e. predicted probabilities) in the image")
			@DefaultValue("false") 
			@QueryParam("addPrediction") 
			boolean addPrediction,

			@Parameter(description = "Add legend (i.e. the color gradient used)")
			@DefaultValue("true")
			@QueryParam("addLegend")
			boolean addLegend,

			@Parameter(description = "Add title to the image (the model name)")
			@DefaultValue("false") 
			@QueryParam("addTitle") 
			boolean addTitle,

			@Context SecurityContext securityContext ) {
		logger.debug("Initial image-size at API-level: imageHeight={}, imageWidth={}",imageHeight,imageWidth);
		try {
			return Predict.doPredictImage(molecule, imageWidth, imageHeight, addPrediction, addTitle, addLegend); 
		} catch (Exception e) {
			return convertToErrorResponse(e);
		}
	}

	
	@Path("/predictImage")
	@POST
	@Consumes( MediaType.TEXT_PLAIN )
	@Produces({ Utils.PNG_MEDIA_TYPE, MediaType.APPLICATION_JSON }) 
	@Operation(
			summary = "Make a prediction image for the given molecule",
			tags = {PREDICT_TAG},
			description = "Predict a given molecule to get a prediction image, accepts SMILES or MDL v2000/v3000 format. In case a MDL is sent, it must be properly URL-encoded in UTF-8. You can use for instance https://www.urlencoder.org/ to encode your file.",
			responses = { 
					@ApiResponse(responseCode = "200", description = "OK", 
							content=@Content(mediaType = Utils.PNG_MEDIA_TYPE)),

					@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(
							schema = @Schema(implementation = BadRequestError.class), mediaType = MediaType.APPLICATION_JSON)),

					@ApiResponse(responseCode = "500", description = "Prediction error", content = @Content(
							schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON)),

					@ApiResponse(responseCode = "503", description = "Service not available", content = @Content(
							schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON)) 
			}
			)
	public Response predictImagePost( 

			@RequestBody(description="Molecule to predict in SMILES or MDL format",
			content = @Content(examples=@ExampleObject(value="CCCCC=O"), mediaType = MediaType.TEXT_PLAIN))
			String molecule,

			@Parameter(description = "Image width in pixels",
				schema = @Schema(maximum=""+Utils.MAX_IMAGE_SIZE, minimum=""+Utils.MIN_IMAGE_SIZE, defaultValue=""+Utils.DEFAULT_IMAGE_WH))
			@DefaultValue(""+Utils.DEFAULT_IMAGE_WH)
			@QueryParam("imageWidth")
			int imageWidth,

			@Parameter(description = "Image height in pixels",
				schema = @Schema(maximum=""+Utils.MAX_IMAGE_SIZE, minimum=""+Utils.MIN_IMAGE_SIZE, defaultValue=""+Utils.DEFAULT_IMAGE_WH)) 
			@DefaultValue(""+Utils.DEFAULT_IMAGE_WH) 
			@QueryParam("imageHeight") 
			int imageHeight,

			@Parameter(description = "Add the prediction (i.e. predicted probabilities) in the image")
			@DefaultValue("false") 
			@QueryParam("addPrediction") 
			boolean addPrediction,

			@Parameter(description = "Add legend (i.e. the color gradient used)")
			@DefaultValue("true")
			@QueryParam("addLegend")
			boolean addLegend,

			@Parameter(description = "Add title to the image (the model name)")
			@DefaultValue("false") 
			@QueryParam("addTitle") 
			boolean addTitle,

			@Context SecurityContext securityContext ) {
		logger.debug("Initial image-size at API-level: imageHeight={}, imageWidth={}",imageHeight,imageWidth);
		try {
			return Predict.doPredictImage(molecule, imageWidth, imageHeight, addPrediction, addTitle, addLegend); 
		} catch (Exception e) {
			return convertToErrorResponse(e);
		}
	}

	private Response convertToErrorResponse(Exception e) {
		return Utils.getResponse(
			new ErrorResponse(jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				"Service failure: " + e.getMessage() + ", please contact the service provider if the error was not due to user-error")
		);
	}
}
