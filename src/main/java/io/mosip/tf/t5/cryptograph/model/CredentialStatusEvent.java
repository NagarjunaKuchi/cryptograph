package io.mosip.tf.t5.cryptograph.model;

import lombok.Data;
@Data
public class CredentialStatusEvent {
	private String publisher;
	private String topic;
	private String publishedOn;
	private StatusEvent event;
}
