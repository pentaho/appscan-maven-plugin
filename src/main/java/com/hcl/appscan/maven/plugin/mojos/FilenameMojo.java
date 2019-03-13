package com.hcl.appscan.maven.plugin.mojos;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;

import com.hcl.appscan.sdk.scanners.sast.SASTConstants;

@Mojo ( name="filename",
        requiresProject=true,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        defaultPhase=LifecyclePhase.INITIALIZE)
public class FilenameMojo extends AppScanMojo {
  
  @Parameter( defaultValue = "${project}", required = true, readonly = true )
  MavenProject project;
  
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    
    List<String> dirs = new ArrayList<String>();
    File userDir = new File( System.getProperty("user.dir") );
    dirs = this.getDirsFromProjectRoot( dirs, userDir );
    String appscanFilename = m_targetDir + File.separator + String.join( "-", dirs ) + SASTConstants.IRX_EXTENSION;
    project.getProperties().setProperty( "appscan-filename", appscanFilename );
    getLog().info( "appscan-filename will be " + project.getProperties().getProperty( "appscan-filename" ) );

  }
  
  
  private List<String> getDirsFromProjectRoot( List<String> dirs, File dir ) {
    File dotGitDir = new File( dir, ".git" );
    if ( !dotGitDir.exists() ) {
      dirs = getDirsFromProjectRoot( dirs, dir.getParentFile() );
    }
    String dirName = dir.getName();
    // SAClientUtils doesn't like the '~' char we use for branch designation in pipelines so change it to '.'
    dirName = dirName.replace( "~", "." );
    dirs.add( dirName );
    return dirs;
  }
  

}
