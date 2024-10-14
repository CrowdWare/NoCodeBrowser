import os
import subprocess
from docs.upd_deploy import update


def run_git_commands():
    try:
        # Add all changes
        subprocess.run(['git', 'add', '.'], check=True)
        
        # Commit changes
        subprocess.run(['git', 'commit', '-m', 'Update deployment descriptor before'], check=True)
        
        # Push to GitHub
        subprocess.run(['git', 'push'], check=True)
        
        print("Changes pushed successfully to GitHub.")
    except subprocess.CalledProcessError as e:
        print(f"An error occurred while running git commands: {e}")

def main():
    curdir = os.getcwd()
    os.chdir("docs")
    update()
    os.chdir(curdir)
    run_git_commands()

if __name__ == "__main__":
    main()