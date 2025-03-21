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
def generate_deployment_data(type, base_path, exclude_files=None):
    if exclude_files is None:
        exclude_files = []

    deployment_entries = []
    for dirpath, _, filenames in os.walk(base_path):
        for filename in filenames:
            if filename not in exclude_files and not filename.startswith('.'):
                file_path = os.path.relpath(os.path.join(dirpath, filename), base_path)
                mod_time = os.path.getmtime(os.path.join(dirpath, filename))
                formatted_time = datetime.utcfromtimestamp(mod_time).strftime('%Y.%m.%d %H.%M.%S')
                deployment_entries.append(f'  File {{ path: "{file_path}" time: "{formatted_time}" type: "{type}" }}')

    return "\n".join(deployment_entries)


def update():
    base_path = os.getcwd()
    app_sml_path = os.path.join(base_path, 'app.sml')

    pages_en_path = os.path.join(base_path, 'pages-en')
    pages_de_path = os.path.join(base_path, 'pages-de')
    pages_es_path = os.path.join(base_path, 'pages-es')
    pages_pt_path = os.path.join(base_path, 'pages-pt')
    pages_fr_path = os.path.join(base_path, 'pages-fr')
    pages_eo_path = os.path.join(base_path, 'pages-eo')
    parts_en_path = os.path.join(base_path, 'parts-en')
    parts_de_path = os.path.join(base_path, 'parts-de')
    parts_es_path = os.path.join(base_path, 'parts-es')
    parts_pt_path = os.path.join(base_path, 'parts-pt')
    parts_fr_path = os.path.join(base_path, 'parts-fr')
    parts_eo_path = os.path.join(base_path, 'parts-eo')
    images_path = os.path.join(base_path, 'images')
    sounds_path = os.path.join(base_path, 'sounds')
    videos_path = os.path.join(base_path, 'videos')
    textures_path = os.path.join(base_path, 'textures')
    models_path = os.path.join(base_path, 'models')

    deployment_data = generate_deployment_data("page-en", pages_en_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("page-de", pages_de_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("page-es", pages_es_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("page-pt", pages_pt_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("page-fr", pages_fr_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("page-eo", pages_eo_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("part-en", parts_en_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("part-de", parts_de_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("part-es", parts_es_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("part-pt", parts_pt_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("part-fr", parts_fr_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("part-eo", parts_eo_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("image", images_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("sound", sounds_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("video", videos_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("texture", textures_path, exclude_files=['.DS_Store'])
    deployment_data += "\n" + generate_deployment_data("model", models_path, exclude_files=['.DS_Store'])

    print("Updating app.sml with deployment files...")
    update_deployment(app_sml_path, deployment_data)


if __name__ == "__main__":
    os.chdir("docs")
    update()
    os.chdir("..")

