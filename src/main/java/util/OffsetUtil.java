package edu.pdx.imagej.reconstruction.util;

public class OffsetUtil {
    public static int get_offset(int offset, int current, int min, int max)
    {
        int result = current + offset;
        return result < min ? (min - current) : result > max ? (max - current) : offset;
    }

    public static int get_multi_offset(int offset, int current, int min_out, int max_out, int min_in, int max_in)
    {
        int result_min = current + offset + min_in - 1;
        int result_max = current + offset + max_in - 1;
        if (result_min < min_out) offset = min_out - min_in - current + 1;
        else if (result_max > max_out) offset = max_out - max_in - current + 1;
        return offset;
    }
}
