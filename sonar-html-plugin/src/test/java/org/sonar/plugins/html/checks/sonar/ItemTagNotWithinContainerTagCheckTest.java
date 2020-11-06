/*
 * SonarSource HTML analyzer :: Sonar Plugin
 * Copyright (c) 2010-2020 SonarSource SA and Matthijs Galesloot
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.html.checks.sonar;


import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.html.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.html.checks.TestHelper;
import org.sonar.plugins.html.visitor.HtmlSourceCode;

public class ItemTagNotWithinContainerTagCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() throws Exception {
    HtmlSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/ItemTagNotWithinContainerTagCheck.html"), new ItemTagNotWithinContainerTagCheck());

    checkMessagesVerifier.verify(sourceCode.getIssues())
        .next().atLocation(1, 0, 1, 4).withMessage("Surround this <li> item tag by a <ul> or <ol> container one.")
        .next().atLocation(4, 0, 4, 4).withMessage("Surround this <DT> item tag by a <dl> container one.")
        .next().atLine(8).withMessage("Surround this <lI> item tag by a <ul> or <ol> container one.")
        .next().atLine(18);
  }

}
