package launchserver.response.update;

import java.io.IOException;

import launcher.request.RequestException;
import launcher.request.update.LauncherRequest;
import launcher.serialize.HInput;
import launcher.serialize.HOutput;
import launcher.serialize.signed.SignedBytesHolder;
import launchserver.LaunchServer;
import launchserver.response.Response;

public final class LauncherResponse extends Response {
	public LauncherResponse(LaunchServer server, long id, HInput input, HOutput output) {
		super(server, id, input, output);
	}

	@Override
	public void reply() throws IOException {
		// Resolve launcher binary
		SignedBytesHolder bytes = (input.readBoolean() ? server.launcherEXEBinary : server.launcherBinary).getBytes();
		if (bytes == null) {
			throw new RequestException("error.launcher.missingBinary");
		}
		writeNoError(output);

		// Write request result
		LauncherRequest.Result result = new LauncherRequest.Result(bytes.getSign(), server.getProfiles());
		result.write(output);
		output.flush();

		// Send binary if requested
		if (input.readBoolean()) {
			output.writeByteArray(bytes.getBytes(), 0);
		}
	}
}
