#
# Copyright (C) 2025 CrowdWare
#
# This file is part of FreeBookDesigner.
#
#  FreeBookDesigner is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  FreeBookDesigner is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with FreeBookDesigner.  If not, see <http://www.gnu.org/licenses/>.
#

import socket
import os
from http.server import SimpleHTTPRequestHandler, HTTPServer
from upd_deploy import update


def get_local_ip_address():
    """Get the locale IP-Adress of the computer."""
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        # Verbindung zu externem Host, um lokale IP zu ermitteln
        s.connect(("8.8.8.8", 80))
        local_ip_address = s.getsockname()[0]
    except Exception:
        local_ip_address = "Unable to get IP address"
    finally:
        s.close()
    return local_ip_address


def start_web_server(port=8000):
    """Starting a HTTP-Webserver."""
    handler = SimpleHTTPRequestHandler
    server = HTTPServer(("", port), handler)

    ip_address = get_local_ip_address()
    if ip_address:
        print(f"Server running at: http://{ip_address}:{port}/")

    print(f"Serving on port {port}")
    server.serve_forever()


if __name__ == "__main__":
    os.chdir("docs")
    print("Updating app.sml with deployment files...")
    update()

    print("Starting the web server...")
    start_web_server(port=8000)
    os.chdir("..")