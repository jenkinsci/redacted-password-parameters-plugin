package io.jenkins.plugins.redactedpasswordparameters;

import hudson.console.LineTransformationOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
* OutputStream wrapper that redacts some text.
*
* <p>There are some flaws with this. If extending OutputStream, the secret can
* be outputted over two calls to write() ad therefore not redacted.
*
* <p>If extended LineTransformationOutputStream, the secret be be multi-line
* (for example an RSA private key) and therefore never get redacted. This seems
* less likely as the password parameter in the Jenkins UI is a single line only.
*/
public class RedactedOutputStream extends LineTransformationOutputStream {
  private static final Logger LOG = Logger.getLogger(RedactedOutputStream.class.getName());
  private final OutputStream out;
  private final Charset charset;
  private final HashSet<Pattern> patternsToRedact;

  RedactedOutputStream(final OutputStream out, final Charset charset) {
    this.out = out;
    this.charset = charset;
    this.patternsToRedact = new HashSet<Pattern>();
  }

  public void addStringToRedact(final String secret) {
    this.patternsToRedact.add(Pattern.compile(secret));
  }

  @Override
  protected void eol(final byte[] bytes, final int len) throws IOException {
    String line = charset.decode(ByteBuffer.wrap(bytes, 0, len)).toString();

    for (Pattern pattern : patternsToRedact) {
      // Four asterisks because that's what credentials-binding-plugin uses.
      line = pattern.matcher(line).replaceAll("****");
    }

    byte[] newLine = charset.encode(line).array();
    out.write(newLine, 0, newLine.length);
  }
}
