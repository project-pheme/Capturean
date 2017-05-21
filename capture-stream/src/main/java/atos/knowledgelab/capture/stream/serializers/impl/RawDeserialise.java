package atos.knowledgelab.capture.stream.serializers.impl;

import atos.knowledgelab.capture.stream.serializers.IDeserialize;

public class RawDeserialise implements IDeserialize<String> {

	@Override
	public String deserialize(String msg) throws Exception {
		return msg;
	}

}
