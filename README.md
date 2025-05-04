# wiredi-web

This project works in the following way:

The core module is a general abstraction of Http integrations.
Based on the core module, there can be any number of backbone modules.

Backbone modules are technology-specific and translate the technology to the general Http abstraction in the core
module.
The core module then translates the Http abstraction to the message abstraction and invokes RequestHandler instances in
the WireRepository.

Provided backbones are:

- **Sun**: uses the com.sun.net.httpserver.HttpServer to handle http requests.