package io.mosip.tf.t5.cryptograph.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import io.mosip.tf.t5.cryptograph.model.BarCodeResponse;
import io.mosip.tf.t5.cryptograph.model.EmailParam;
import io.mosip.tf.t5.cryptograph.model.Pipeline;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Service
public class IDEncodeCaller {
	
	public void uploadData(Map<String, Object> demoAttributes, Map<String, Object> bioAttributes) {
		ArrayList<MultipartBody.Part> parts = new ArrayList<>();
		parts.add(Utilities.byteArrayToMultipartFile("demog", getDemoAttributesAsString(demoAttributes).getBytes(),
				"demo.json", "application/octet-stream"));
		Pipeline pipeline = new Pipeline();
		 EmailParam emailParams = new EmailParam();
	        emailParams.setEmailTol("nagarjunabtechece@gmail.com");
	        emailParams.subject = "Your Tech5 IDencode";	        
	        pipeline.setEmailParams(emailParams);
		for (Map.Entry<String,Object> e : bioAttributes.entrySet()) {
			parts.add(Utilities.byteArrayToMultipartFile(e.getKey().toString(), e.getValue().toString().getBytes(), e.getKey().toString() +".template", "application/octet-stream"));			
	    }

		String pipelineJson = new Gson().toJson(pipeline);
		parts.add(Utilities.byteArrayToMultipartFile("pipeline", pipelineJson.getBytes(), "pipeline.json", "application/json"));
		
        if(parts.size()> 0) {
        	T5Service service = RetrofitClientInstance.getRetrofitInstance().create(T5Service.class);
        	Call<BarCodeResponse> call = service.createBarCode("https://idencode.tech5-sa.com/v1/enroll", parts);
            call.enqueue(new Callback<BarCodeResponse>() {
                @Override
                public void onResponse(Call<BarCodeResponse> call, Response<BarCodeResponse> response) {
                    if (response != null) {
                        if (response.code() == 400) {
                            try { 
                                System.out.println(response.errorBody().string());                                
                            } catch (IOException e) {
                            	System.out.println(e.getStackTrace());
                            }

                        } else if (response.code() == 200 && response.body() != null){
                        	System.out.println(response.body().toString());
                        	getResponse(response);
                        } else {
                            
                        }

                    } else {
                    }
                }

                public void onFailure(Call<BarCodeResponse> call, Throwable t) {
                	System.out.println(t.getLocalizedMessage());
                }
            });
        }        
	}

	private String getDemoAttributesAsString(Map<String, Object> demoAttributes) {
		return StringUtils.join(demoAttributes);
	}
	
	private void writeToFile(String fineName, byte[] data) {				
				try {
					  File pdfFile = new File(
							 fineName
							  ); 
							  OutputStream os = new FileOutputStream(pdfFile); 
							  os.write(data);
							  os.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	}
	
	private void getResponse(Response<BarCodeResponse> response) {	
		BarCodeResponse responseFromServer =response.body();		
		writeToFile("D://" + responseFromServer.getUuid(), Base64.getDecoder().decode(responseFromServer.getImage()));
		writeToFile("D://response" + responseFromServer.getUuid(), response.body().toString().getBytes());
	}


}