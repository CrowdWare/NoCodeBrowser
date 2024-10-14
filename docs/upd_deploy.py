#
# Copyright (C) 2024 CrowdWare
#
# This file is part of NoCodeDesigner.
#
#  NoCodeDesigner is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  NoCodeDesigner is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with NoCodeDesigner.  If not, see <http://www.gnu.org/licenses/>.
#

import os
import re
from datetime import datetime


"""add file to the deploment descriptor"""
def update_deployment(app_sml_path, deployment_data):
    with open(app_sml_path, 'r') as file:
        app_sml_content = file.read()

    deployment_section_regex = re.compile(r"// deployment start.*?// deployment end", re.DOTALL)

    deployment_block = f"""// deployment start - don't edit here
Deployment {{
{deployment_data}
}}
// deployment end"""

    if deployment_section_regex.search(app_sml_content):
        app_sml_content = deployment_section_regex.sub(deployment_block, app_sml_content)
    else:
        app_sml_content = app_sml_content.rstrip('}') + '\n' + deployment_block + '\n}'

    with open(app_sml_path, 'w') as file:
        file.write(app_sml_content)
        
        
"""gets a list of files and there last change date"""
def generate_deployment_data(base_path, exclude_files=None):
    if exclude_files is None:
        exclude_files = []

    deployment_entries = []
    for dirpath, _, filenames in os.walk(base_path):
        for filename in filenames:
            if filename not in exclude_files and not filename.startswith('.'):
                file_path = os.path.relpath(os.path.join(dirpath, filename), base_path)
                mod_time = os.path.getmtime(os.path.join(dirpath, filename))
                formatted_time = datetime.utcfromtimestamp(mod_time).strftime('%Y.%m.%d %H.%M.%S')
                deployment_entries.append(f'  File {{ path: "{file_path}" time: "{formatted_time}" }}')

    return "\n".join(deployment_entries)


def update():
    base_path = os.getcwd()
    app_sml_path = os.path.join(base_path, 'app.sml')

    pages_path = os.path.join(base_path, 'pages')
    assets_path = os.path.join(base_path, 'assets')

    deployment_data = generate_deployment_data(pages_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data(assets_path, exclude_files=['.DS_Store'])

    print("Updating app.sml with deployment files...")
    update_deployment(app_sml_path, deployment_data)
    
    
if __name__ == "__main__":
    update()

   