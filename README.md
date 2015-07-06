Unnamed sprinkler-control-project
=================================


What is it?
-----------

This currently unnamed project is the beginning of a Raspberry Pi based sprinkler controller.  It
is initially intended to run as a standalone application on the Raspberry Pi with a web interface
using an embedded Tomcat or Winstone server.


Contributors
------------

* [Stephen Legge](https://bitbucket.org/stephenalegge)


Retrieve Source
---------------

To clone Unnamed sprinkler-control-project:

    git clone git@bitbucket.org:aleggeup/sprinkler-control-project.git


Building
--------

There is a 'reactor' pom in the 'server' directory off of the root of the project.

`cd server`
`mvn clean package`


Executing
---------

A successful build will create `./server/sprinkler-control-webapp/target/sprinkler-control-webapp-{version}-war-exec.jar`

Run `java -jar sprinkler-control-webapp-{version}-war-exec.jar`

This will run a web server which you can view by browsing to `http://localhost:8080/`


System requirements
-------------------

All you need to build this project is Java 1.7+ and Maven 3.0+.
To run, all you need is Java 1.7+.


Disclaimer
----------

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

