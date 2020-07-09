package org.yeastrc.limelight.xml.taggraph.builder;

import org.yeastrc.limelight.limelight_import.api.xml_dto.*;
import org.yeastrc.limelight.limelight_import.api.xml_dto.ReportedPeptide.ReportedPeptideAnnotations;
import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.limelight.limelight_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;
import org.yeastrc.limelight.xml.taggraph.annotations.PSMAnnotationTypeSortOrder;
import org.yeastrc.limelight.xml.taggraph.annotations.PSMAnnotationTypes;
import org.yeastrc.limelight.xml.taggraph.annotations.PSMDefaultVisibleAnnotationTypes;
import org.yeastrc.limelight.xml.taggraph.constants.Constants;
import org.yeastrc.limelight.xml.taggraph.objects.ConversionParameters;
import org.yeastrc.limelight.xml.taggraph.objects.TagGraphReportedPeptide;
import org.yeastrc.limelight.xml.taggraph.objects.TagGraphResults;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Map;


public class XMLBuilder {

	public void buildAndSaveXML(ConversionParameters conversionParameters,
								Map<String, BigDecimal> staticMods,
								TagGraphResults results)
    throws Exception {

		LimelightInput limelightInputRoot = new LimelightInput();

		limelightInputRoot.setFastaFilename( conversionParameters.getFastaFile().getName() );
		
		// add in the conversion program (this program) information
		ConversionProgramBuilder.createInstance().buildConversionProgramSection( limelightInputRoot, conversionParameters);
		
		SearchProgramInfo searchProgramInfo = new SearchProgramInfo();
		limelightInputRoot.setSearchProgramInfo( searchProgramInfo );
		
		SearchPrograms searchPrograms = new SearchPrograms();
		searchProgramInfo.setSearchPrograms( searchPrograms );

		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );
				
			searchProgram.setName( Constants.PROGRAM_NAME );
			searchProgram.setDisplayName( Constants.PROGRAM_NAME );
			searchProgram.setVersion( results.getVersion() );
			
			
			//
			// Define the annotation types present in the data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );
			
			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
			
			for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes() ) {
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}
			
			//todo: add in non-filterable annotations
		}
		
		
		//
		// Define which annotation types are visible by default
		//
		DefaultVisibleAnnotations xmlDefaultVisibleAnnotations = new DefaultVisibleAnnotations();
		searchProgramInfo.setDefaultVisibleAnnotations( xmlDefaultVisibleAnnotations );
		
		VisiblePsmAnnotations xmlVisiblePsmAnnotations = new VisiblePsmAnnotations();
		xmlDefaultVisibleAnnotations.setVisiblePsmAnnotations( xmlVisiblePsmAnnotations );

		for( SearchAnnotation sa : PSMDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes() ) {
			xmlVisiblePsmAnnotations.getSearchAnnotation().add( sa );
		}
		
		//
		// Define the default display order in proxl
		//
		AnnotationSortOrder xmlAnnotationSortOrder = new AnnotationSortOrder();
		searchProgramInfo.setAnnotationSortOrder( xmlAnnotationSortOrder );
		
		PsmAnnotationSortOrder xmlPsmAnnotationSortOrder = new PsmAnnotationSortOrder();
		xmlAnnotationSortOrder.setPsmAnnotationSortOrder( xmlPsmAnnotationSortOrder );
		
		for( SearchAnnotation xmlSearchAnnotation : PSMAnnotationTypeSortOrder.getPSMAnnotationTypeSortOrder() ) {
			xmlPsmAnnotationSortOrder.getSearchAnnotation().add( xmlSearchAnnotation );
		}
		
		//
		// Define the static mods
		//
		if( staticMods != null && staticMods.keySet().size() > 0 ) {
			StaticModifications smods = new StaticModifications();
			limelightInputRoot.setStaticModifications( smods );
			
			
			for( String residue : staticMods.keySet() ) {
				
				StaticModification xmlSmod = new StaticModification();
				xmlSmod.setAminoAcid( String.valueOf( residue ) );
				xmlSmod.setMassChange( staticMods.get(residue) );
				
				smods.getStaticModification().add( xmlSmod );
			}
		}

		//
		// Define the peptide and PSM data
		//
		ReportedPeptides reportedPeptides = new ReportedPeptides();
		limelightInputRoot.setReportedPeptides( reportedPeptides );
		
		// iterate over each distinct reported peptide
		for( TagGraphReportedPeptide reportedPeptide : results.getPeptidePSMMap().keySet() ) {

			ReportedPeptide xmlReportedPeptide = new ReportedPeptide();
			reportedPeptides.getReportedPeptide().add( xmlReportedPeptide );
			
			xmlReportedPeptide.setReportedPeptideString( reportedPeptide.getReportedPeptideString() );
			xmlReportedPeptide.setSequence( reportedPeptide.getNakedPeptide() );
			
			// add in the filterable peptide annotations (e.g., q-value)
			ReportedPeptideAnnotations xmlReportedPeptideAnnotations = new ReportedPeptideAnnotations();
			xmlReportedPeptide.setReportedPeptideAnnotations( xmlReportedPeptideAnnotations );

			// add in the mods for this peptide
			if( reportedPeptide.getMods() != null && reportedPeptide.getMods().keySet().size() > 0 ) {

				PeptideModifications xmlModifications = new PeptideModifications();
				xmlReportedPeptide.setPeptideModifications( xmlModifications );

				for( int position : reportedPeptide.getMods().keySet() ) {
					PeptideModification xmlModification = new PeptideModification();
					xmlModifications.getPeptideModification().add( xmlModification );

					xmlModification.setMass( reportedPeptide.getMods().get( position ).stripTrailingZeros().setScale( 0, RoundingMode.HALF_UP ) );
					xmlModification.setPosition( BigInteger.valueOf( position ) );
				}
			}

			// add in the PSMs and annotations
			Psms xmlPsms = new Psms();
			xmlReportedPeptide.setPsms( xmlPsms );

			// iterate over all PSMs for this reported peptide

			for( int scanNumber : cometResults.getPeptidePSMMap().get(reportedPeptide).keySet() ) {

				CometPSM psm = cometResults.getPeptidePSMMap().get(reportedPeptide).get( scanNumber );

				// skip this PSM if it's a decoy
				if( psm.getDecoy() ) {
					continue;
				}

				Psm xmlPsm = new Psm();
				xmlPsms.getPsm().add( xmlPsm );

				xmlPsm.setScanNumber( new BigInteger( String.valueOf( scanNumber ) ) );
				xmlPsm.setPrecursorCharge( new BigInteger( String.valueOf( psm.getCharge() ) ) );

				// add in the filterable PSM annotations (e.g., score)
				FilterablePsmAnnotations xmlFilterablePsmAnnotations = new FilterablePsmAnnotations();
				xmlPsm.setFilterablePsmAnnotations( xmlFilterablePsmAnnotations );

				// handle comet PTM scores
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_FDR );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );


					DecimalFormat formatter = new DecimalFormat("0.###E0");

					double fdr = targetDecoyAnalysis.getFDRForScore( psm.getEvalue() );

					BigDecimal bd = BigDecimal.valueOf( fdr );
					bd = bd.round( new MathContext( 3 ) );

					xmlFilterablePsmAnnotation.setValue( bd );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_EVALUE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getEvalue() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_DELTACN );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getDeltaCn() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_DELTACNSTAR );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getDeltaCnStar() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_MASSDIFF );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getMassDiff() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_SPRANK );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getSprank() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_SPSCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getSpscore() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_XCORR );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getXcorr() );
				}


				// add in the mods for this psm
				if( psm.getModifications() != null && psm.getModifications().keySet().size() > 0 ) {

					PsmModifications xmlPSMModifications = new PsmModifications();
					xmlPsm.setPsmModifications( xmlPSMModifications );

					for( int position : psm.getModifications().keySet() ) {
						PsmModification xmlPSMModification = new PsmModification();
						xmlPSMModifications.getPsmModification().add( xmlPSMModification );

						xmlPSMModification.setMass( psm.getModifications().get( position ) );
						xmlPSMModification.setPosition( new BigInteger( String.valueOf( position ) ) );
					}
				}

				// add in open mod for this PSM
				{
					PsmOpenModification xmlPsmOpenMod = new PsmOpenModification();
					xmlPsmOpenMod.setMass(psm.getMassDiff());
					xmlPsm.setPsmOpenModification(xmlPsmOpenMod);
				}
				
				
			}// end iterating over psms for a reported peptide
		
		}//end iterating over reported peptides


		
		
		// add in the matched proteins section
		MatchedProteinsBuilder.getInstance().buildMatchedProteins(
				                                                   limelightInputRoot,
				                                                   conversionParameters.getFastaFile(),
																   cometResults,
																   DecoyUtils.getDecoyPrefixToUse( cometTPPParameters, conversionParameters )
				                                                  );
		
		
		// add in the config file(s)
		ConfigurationFiles xmlConfigurationFiles = new ConfigurationFiles();
		limelightInputRoot.setConfigurationFiles( xmlConfigurationFiles );
		
		ConfigurationFile xmlConfigurationFile = new ConfigurationFile();
		xmlConfigurationFiles.getConfigurationFile().add( xmlConfigurationFile );
		
		xmlConfigurationFile.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
		xmlConfigurationFile.setFileName( conversionParameters.getFonfFile().getName() );
		xmlConfigurationFile.setFileContent( Files.readAllBytes( FileSystems.getDefault().getPath( conversionParameters.getFonfFile().getAbsolutePath() ) ) );
		
		
		//make the xml file
		CreateImportFileFromJavaObjectsMain.getInstance().createImportFileFromJavaObjectsMain( conversionParameters.getLimelightXMLOutputFile(), limelightInputRoot);
		
	}

	
}
