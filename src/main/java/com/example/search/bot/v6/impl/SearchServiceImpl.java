package main.java.com.example.search.bot.v6.impl;

import main.java.com.example.core.FileReader;
import main.java.com.example.core.SearchService;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchServiceImpl implements SearchService {

    private final FileReader fileReader;
    private final Pattern pattern;

    public SearchServiceImpl(FileReader fileReader, Pattern pattern) {
        this.fileReader = fileReader;
        this.pattern = pattern;
    }

    public Map<String, List<String>> buildInheritanceIndex(String projectPath, Pattern pattern) throws Exception {
        Configuration conf = new Configuration();
        conf.set("pattern", pattern.toString());

        Job job = Job.getInstance(conf, "Inheritance Index");
        job.setJarByClass(SearchServiceImpl.class);
        job.setMapperClass(InheritanceMapper.class);
        job.setReducerClass(InheritanceReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(projectPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        job.waitForCompletion(true);

        return readOutput(outputPath);
    }

    private Map<String, List<String>> readOutput(String outputPath) throws IOException {
        Map<String, List<String>> inheritanceIndex = new HashMap<>();
        // Реализация чтения результата из outputPath
        return inheritanceIndex;
    }

    public static class InheritanceMapper extends Mapper<Object, Text, Text, Text> implements main.java.com.example.search.bot.v6.impl.InheritanceMapper {

        private Pattern pattern;

        @Override
        public void setup(Context context) {
            Configuration conf = context.getConfiguration();
            this.pattern = Pattern.compile(conf.get("pattern"));
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Matcher matcher = pattern.matcher(value.toString());
            while (matcher.find()) {
                String className = matcher.group(2);
                String parentName = matcher.group(4);
                if (className != null) {
                    context.write(new Text(className), new Text(parentName));
                }
            }
        }
    }

    public static class InheritanceReducer extends Reducer<Text, Text, Text, Text> implements main.java.com.example.search.bot.v6.impl.InheritanceReducer {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            List<String> parents = new ArrayList<>();
            for (Text val : values) {
                parents.add(val.toString());
            }
            context.write(key, new Text(String.join(",", parents)));
        }
    }
}
