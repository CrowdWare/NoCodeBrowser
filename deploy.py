import os
import subprocess
import argparse
from docs.upd_deploy import update


def run_git_commands(desc):
    try:
        # Add all changes
        subprocess.run(['git', 'add', '.'], check=True)
        
        # Commit changes
        subprocess.run(['git', 'commit', '-m', desc], check=True)
        
        # Push to GitHub
        subprocess.run(['git', 'push'], check=True)
        
        print("Changes pushed successfully to GitHub.")
    except subprocess.CalledProcessError as e:
        print(f"An error occurred while running git commands: {e}")

def main():
    parser = argparse.ArgumentParser(description="Script to update and run git commands.")
    parser.add_argument("desc", help="Description for the git commit message")
    
    # Argumente parsen
    args = parser.parse_args()

    curdir = os.getcwd()
    os.chdir("docs")
    update()
    os.chdir(curdir)
    run_git_commands(desc=args.desc)

if __name__ == "__main__":
    main()