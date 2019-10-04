package io.swagger.api;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.genettasoft.api.predict.Predict;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.api.factories.PredictApiServiceFactory;
import io.swagger.model.BadRequestError;
import io.swagger.model.VenaberResult;
import io.swagger.model.Error;

@Path("/")

@Api()
public class PredictApi  {
	private final PredictApiService delegate;

	public PredictApi(@Context ServletConfig servletContext) {
		PredictApiService delegate = null;

		if (servletContext != null) {
			String implClass = servletContext.getInitParameter("PredictApi.implementation");
			if (implClass != null && !"".equals(implClass.trim())) {
				try {
					delegate = (PredictApiService) Class.forName(implClass).newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} 
		}

		if (delegate == null) {
			delegate = PredictApiServiceFactory.getPredictApi();
		}

		this.delegate = delegate;
	}

	@Path("/predict")
	@GET
	@Consumes({ "multipart/form-data" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Make a prediction on a given molecule", 
	notes = "Predict a given molecule in SMILES, MDL v2000 or MDL v3000 format. In case a MDL is sent, it must be properly URL-encoded in UTF-8. You can use for instance https://www.urlencoder.org/ to encode your file.", 
	response = Void.class, 
	tags={"Predict"})
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = VenaberResult.class),

			@ApiResponse(code = 400, message = "Bad Request", response = BadRequestError.class),

			@ApiResponse(code = 500, message = "Prediction error", response = Error.class),

			@ApiResponse(code = 503, message = "Service not available", response = Error.class) })
	public Response predictGet(
			@ApiParam(value = "Compound structure notation using SMILES or MDL format", 
			required=false,
			defaultValue="CCCCC=O")
			@QueryParam("molecule") String molecule,
			
			@ApiParam(value = "**(Depricated)** Compound structure notation using SMILES notation", required=false)
			@QueryParam("smiles") String smiles,
			
			@Context SecurityContext securityContext)
					throws NotFoundException {
		if (smiles!=null && !smiles.isEmpty()) // TODO - remove in newer versions!
			return delegate.predictPost(smiles,securityContext);
		else 
			return delegate.predictPost(molecule, securityContext);
	}


	@Path("/predictImage")
	@GET
	@Consumes({ "multipart/form-data" })
	@Produces({"image/png", "application/json"})
	@ApiOperation(value = "Make a prediction image for the given molecule", 
	notes = "Predict a given molecule to get a prediction image, accepts SMILES, MDL v2000 or MDL v3000 format. In case a MDL is sent, it must be properly URL-encoded in UTF-8. You can use for instance https://www.urlencoder.org/ to encode your file.",
	response = Void.class, 
	tags={"Predict"})
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK"),

			@ApiResponse(code = 400, message = "Bad Request", response = BadRequestError.class),

			@ApiResponse(code = 500, message = "Prediction error", response = Error.class),

			@ApiResponse(code = 503, message = "Service not available", response = Error.class) })
	public Response predictImageGet( 
			@ApiParam(value = "Compound structure notation using SMILES or MDL format", 
			required=false,
			defaultValue="CCCCC=O")
			@QueryParam("molecule") String molecule, // MOLECULE
			
			@ApiParam(value = "**(Depricated)** Compound structure notation using SMILES notation", required=false)
			@QueryParam("smiles") String smiles, // SMILES - DEPRECATED
			
			@ApiParam(value = "Image width (min 50 pixels, max 5000 pixels)", allowableValues="range[50,5000]")
			@DefaultValue("600") @QueryParam("imageWidth") 
			int imageWidth,
			
			@ApiParam(value = "Image height (min 50 pixels, max 5000 pixels)", allowableValues="range[50,5000]")
			@DefaultValue("600") @QueryParam("imageHeight") 
			int imageHeight,
			
			@ApiParam(value = "Write probability in figure")
			@DefaultValue("false") @QueryParam("addProbability") 
			boolean addPvals,
			
			@ApiParam(value = "Add title to the image (using the model name)")
			@DefaultValue("false") @QueryParam("addTitle") 
			boolean addTitle,
			
			@Context SecurityContext securityContext ) {
		if (smiles!=null && !smiles.isEmpty()) // TODO - remove in newer versions!
			return delegate.predictImagePost(smiles, imageWidth, imageHeight, addPvals, addTitle, securityContext);
		else
			return delegate.predictImagePost(molecule, imageWidth, imageHeight, addPvals, addTitle, securityContext);
	}
	
	@Path("/health")
	@GET
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 500, message = "Prediction error", response = Error.class),
	})
	public Response health() {
		return Predict.checkHealth();
	}
}