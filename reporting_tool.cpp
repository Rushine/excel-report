// reporting_tool.cpp : Defines the entry point for the console application.
//

#include "data.hpp"
#include "xml_dump.hpp"
#include "common_parse.hpp"

TestSession *current_session;
int size[LAST_SECTION];
int warn_level;
bool core_only, ext_only;
bool Debug_on;
bool Cruise_Control;

void man() {
	printf("Syntax is:\n\treport [Options] <log directory>+\n ");
	printf("Options are:\n ");
	printf("\t-h | -help | --help : print this help message\n ");
	printf("\t-o <file> : redirect output to <file>\n ");
	printf("\t-i <option> : option to ignore\n");
	printf("\t-noccopt : ignore all compiler options\n");
	printf("\t-noobj : do not generate size sheet on object\n");
	printf("\t-nobin : do not generate size sheet on binaries\n");
	printf("\t-nocycles : do not generate size sheet on cycles\n");
	printf("\t-noext : do not generate sheet on extensions\n");
	printf("\t-nocore : do not generate sheet on core\n");
	printf("\t-s <section> : size on this section, valid section are: \n");
	printf("\t-ref <fichier> : list of references directory to parse: \n");
	printf("\t\ttext/data/rodata/bss/symtab/strtab/dbg/stxp70/total\n");
	printf("\t\ttext_rodata : to combine text+rodata\n");
	printf("\t-key <name> : directory name to consider under referenced directory from ref option \n");
	exit(0);
}

void decode_section(char *n) {
	if (!strcmp(n, "text")) {
		size[TEXT]=1;
		return;
	}
	if (!strcmp(n, "data")) {
		size[DATA]=1;
		return;
	}
	if (!strcmp(n, "rodata")) {
		size[RODATA]=1;
		return;
	}
	if (!strcmp(n, "bss")) {
		size[BSS]=1;
		return;
	}
	if (!strcmp(n, "symtab")) {
		size[SYMTAB]=1;
		return;
	}
	if (!strcmp(n, "strtab")) {
		size[STRTAB]=1;
		return;
	}
	if (!strcmp(n, "dbg")) {
		size[DBG_SECTION]=1;
		return;
	}
	if (!strcmp(n, "rela_text")) {
		size[RELA_TEXT]=1;
		return;
	}
	if (!strcmp(n, "rela_data")) {
		size[RELA_DATA]=1;
		return;
	}
	if (!strcmp(n, "rela_rodata")) {
		size[RELA_RODATA]=1;
		return;
	}
	if (!strcmp(n, "rela_dbg")) {
		size[RELA_DBG_FRAME]=1;
		return;
	}
	if (!strcmp(n, "stxp70")) {
		size[STXP70_EXTRECONF_INFO]=1;
		return;
	}
	if (!strcmp(n, "total")) {
		size[TOTAL]=1;
		return;
	}
	if (!strcmp(n, "text_rodata")) {
		size[RODATA_PLUS_TEXT]=1;
		return;
	}
	fprintf(stderr,"ERROR, This size section/combination is not allowed\n");
	exit(1);
}

int main(int argc, char* argv[]) {
	int i;
	Excel_Output *output=NULL;
	char filename[1024];
	char *ref_file=NULL, *key_name=NULL;
	RootDataClass rootdata=RootDataClass();
	SummaryClass summary=SummaryClass();
	bool obj_size = true;
	bool bin_size = true;
	bool cycle = true;
	bool compare_options = true;
	bool generate_output_info = true;
	int size_obj_data=0;
	int size_bin_data=0;
	int cycle_data=0;
	int nb_tst=0;
	Cruise_Control=false;
	NameList *test_names = new NameList;
	warn_level=1;
	core_only=false;
	ext_only=false;
	for (i=0; i<LAST_SECTION; i++)
		size[i]=0;
	/*Useless flag to ignore*/
	rootdata.add_ignored_flags("-keep");
	rootdata.add_ignored_flags("-Mkeepasm");
	rootdata.add_ignored_flags("-fno-verbose-asm");
	Debug_on=false;
	if (argc==1) {
		man();
		exit(0);
	}

	while (--argc > 0) {
		++argv;
		if (!strcmp((*argv), "-o")) {
			if (--argc > 0) {
				++argv;
				output = new Excel_Output(*argv);
			}
		} else if (!strcmp((*argv), "-h") || !strcmp((*argv), "-help")
				|| !strcmp((*argv), "--help")) {
			man();
			exit(0);
		} else if (!strcmp((*argv), "-i")) {
			if (--argc > 0) {
				++argv;
				rootdata.add_ignored_flags(*argv);
			}
		} else if (!strcmp((*argv), "-s")) {
			if (--argc > 0) {
				++argv;
				decode_section(*argv);
			}
		} else if (!strcmp((*argv), "-ref")) {
			if (--argc > 0) {
				++argv;
				ref_file=strdup(*argv);
			}
		} else if (!strcmp((*argv), "-key")) {
			if (--argc > 0) {
				++argv;
				key_name=strdup(*argv);
			}
		} else if (!strcmp((*argv), "-Woff")) {
			warn_level=0;
		} else if (!strcmp((*argv), "-noobj")) {
			obj_size=false;
		} else if (!strcmp((*argv), "-nobin")) {
			bin_size=false;
		} else if (!strcmp((*argv), "-nocycles")) {
			cycle=false;
		} else if (!strcmp((*argv), "-noccopt")) {
			compare_options=false;
		} else if (!strcmp((*argv), "-noext")) {
			core_only=true;
		} else if (!strcmp((*argv), "-nocore")) {
			ext_only=true;
		} else if (!strcmp((*argv), "-dbg")) {
			Debug_on=true;
		} else if (!strcmp((*argv), "-cruisec")) {
			Cruise_Control=true;
		} else {
			test_names->push_back(strdup(*argv));
			nb_tst++;
		}
	}

	/*Ref treatment*/
	if (ref_file) {
		if (!key_name)
			if (nb_tst > 1) {
				fprintf(stderr,"ERROR, When ref file and several command line tests dirs, a key is needed\n");
				exit(1);
			}
		key_name = strdup(*(test_names->begin()));
		FILE *my_ref_file = fopen(ref_file, "r");
		if (my_ref_file==NULL) {
			fprintf(stderr,"ERROR, Specified ref file does not exist\n");
			exit(1);
		}
		char tmp_name[2048];
		char *session_name;
		char *pt;
		while (!feof(my_ref_file)) {
			fscanf(my_ref_file, "%s\n", tmp_name);
			if (tmp_name[strlen(tmp_name)-1]=='/')
				tmp_name[strlen(tmp_name)-1]='\0';

			pt=strrchr(tmp_name, '/');
			if (pt) {
				pt++;
				session_name=strdup(pt);
			} else {
				session_name=strdup(tmp_name);
			}
			strcat(tmp_name, "/");
			strcat(tmp_name, key_name);
			sprintf(filename, "%s/INFO", tmp_name);
			FILE *my_tmp_file = fopen(filename, "r");
			if (!my_tmp_file) {
				if (warn_level)
					fprintf(stderr,"WARNING: Directory %s does not contain any info\n",tmp_name);
			} else {
				rootdata.add_session(tmp_name, session_name);
				fclose(my_tmp_file);
			}
		}
		fclose(my_ref_file);
	}
	ForEachPt(test_names,iter_name) {
		rootdata.add_session(*iter_name, *iter_name);
	}
	/**/
	
	ForEachPt(rootdata.get_sessions(),iter_session) {
		current_session = *iter_session;
		char tmp_name[2048];
		sprintf(tmp_name,"%s/SUBTESTS",current_session->get_path());
		FILE *my_tmp_file = fopen(tmp_name, "r");
		if (!my_tmp_file) {
			sprintf(filename,"%s/INFO",current_session->get_path());
			if (Debug_on) printf("Parse INFO : %s, %d\n",filename,__LINE__);
			parse_info(filename);
			sprintf(filename,"%s/FAILED",current_session->get_path());
			if (Debug_on) printf("Parse FAIL : %s, %d\n",filename,__LINE__);
			parse_fail(filename);
			if(obj_size) {
				sprintf(filename,"%s/codeSize.txt",current_session->get_path());
				if (Debug_on) printf("Parse Codesize : %s, %d\n",filename,__LINE__);
				parse_size(filename,SIZE_OBJ);
			}
			if(bin_size) {
				sprintf(filename,"%s/BinaryCodeSize.txt",current_session->get_path());
				if (Debug_on) printf("Parse Binary Codesize : %s, %d\n",filename,__LINE__);
				parse_size(filename,SIZE_BIN);
			}
			if(cycle) {
				sprintf(filename,"%s/cyclesCount.txt",current_session->get_path());
				if (Debug_on) printf("Parse Cycles : %s, %d\n",filename,__LINE__);
				parse_cycle(filename);
			}
		} else {
			//Here we have subdirs for tests.
			bool first_test=true;
			while (!feof(my_tmp_file)) {
				fscanf(my_tmp_file, "%s\n", tmp_name);
				if (first_test) {
					sprintf(filename,"%s/%s/INFO",current_session->get_path(),tmp_name);
					if (Debug_on) printf("Parse INFO : %s, %d\n",filename,__LINE__);
					parse_info(filename);
					first_test=false;
				}
				sprintf(filename,"%s/%s/FAILED",current_session->get_path(),tmp_name);
				if (Debug_on) printf("Parse FAIL : %s, %d\n",filename,__LINE__);
				parse_fail(filename);
				if(obj_size) {
					sprintf(filename,"%s/%s/codeSize.txt",current_session->get_path(),tmp_name);
					if (Debug_on) printf("Parse Codesize : %s, %d\n",filename,__LINE__);
					parse_size(filename,SIZE_OBJ);
				}
				if(bin_size) {
					sprintf(filename,"%s/%s/BinaryCodeSize.txt",current_session->get_path(),tmp_name);
					if (Debug_on) printf("Parse Binary Codesize : %s, %d\n",filename,__LINE__);
					parse_size(filename,SIZE_BIN);
				}
				if(cycle) {
					sprintf(filename,"%s/%s/cyclesCount.txt",current_session->get_path(),tmp_name);
					if (Debug_on) printf("Parse Cycles : %s, %d\n",filename,__LINE__);
					parse_cycle(filename);
				}
			}				
		}
	}

	if (!output)
		output = new Excel_Output("default_output.xls");

	output->generate_header();
	output->create_summary(rootdata);
	rootdata.remove_ignored_flags(compare_options);

	for (i=0; i<LAST_SECTION; i++) {
		if (size[i])
			break;
	}
	if (i==LAST_SECTION)
		size[TEXT]=1;

	if (generate_output_info) {
		ForEachRPt(rootdata.get_sessions(),iter_session) {
			summary.add_session((*iter_session)->get_path(), (*iter_session)->get_name());
//			printf("Add : %s, %s\n",(*iter_session)->get_path(), (*iter_session)->get_name());
		}
	}

	
	/* For a better summary*/
	if (obj_size) {
		rootdata.compute_data(SIZE_OBJ);
		size_obj_data = rootdata.get_nb_max_data();
		if (generate_output_info) {
			ForEachPt(rootdata.get_disc(),iter) {
				ForEachPt(rootdata.get_sessions(),iter_session) {
					summary.add_summary_value((*iter_session)->get_path(),(*iter_session)->get_name(),(*iter)->get_test(),SIZE_OBJ,(*iter_session)->get_size(*iter,RODATA_PLUS_TEXT,SIZE_OBJ));
				}
			}
		}
	}
	if (bin_size) {
		rootdata.compute_data(SIZE_BIN);
		size_bin_data = rootdata.get_nb_max_data();
		if (generate_output_info) {
			ForEachPt(rootdata.get_disc(),iter) {
				ForEachPt(rootdata.get_sessions(),iter_session) {
					summary.add_summary_value((*iter_session)->get_path(),(*iter_session)->get_name(),(*iter)->get_test(),SIZE_BIN,(*iter_session)->get_size(*iter,RODATA_PLUS_TEXT,SIZE_BIN));
				}
			}
		}
	}
	if (cycle) {
		rootdata.compute_data(SPEED);
		cycle_data = rootdata.get_nb_max_data();
		if (generate_output_info) {
			ForEachPt(rootdata.get_disc(),iter) {
				ForEachPt(rootdata.get_sessions(),iter_session) {
					summary.add_summary_value((*iter_session)->get_path(),(*iter_session)->get_name(),(*iter)->get_test(),SPEED,(*iter_session)->get_cycle(*iter));
				}
			}
		}
	}

	output->create_summary_ver2(rootdata, &size[0], obj_size, size_obj_data, bin_size, size_bin_data, cycle, cycle_data, summary);

	/* End */

	/*Size sheet generation*/
	if (obj_size) {
		rootdata.compute_data(SIZE_OBJ);
		for (i=0; i<LAST_SECTION; i++) {
			if (size[i])
				output->create_page(rootdata, SIZE_OBJ, (Section)i);
		}
	}

	if (bin_size) {
		rootdata.compute_data(SIZE_BIN);
		for (i=0; i<LAST_SECTION; i++) {
			if (size[i])
				output->create_page(rootdata, SIZE_BIN, (Section)i);
		}
	}

	/*Speed sheet generation*/

	if (cycle) {
		rootdata.compute_data(SPEED);
		output->create_page(rootdata, SPEED, LAST_SECTION);
	}

	//  ForEachPt(rootdata.get_sessions(),iter_session) {
	//    (*iter_session)->dump_info();
	//  }

	output->excel_terminate();

	if (generate_output_info)
		if(Cruise_Control) summary.dump_summary_cc();
		else summary.dump_summary();
	return 0;
}

