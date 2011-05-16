#!/usr/bin/perl

use strict;
use warnings;
my @files = glob("geo*.txt");

foreach ( @files ) {
  open my $fh, "<", $_;
  my @coord_str = <$fh>;
  close $fh;

  my @coords;
  foreach my $s ( @coord_str ) {
    push @coords, [ $s =~ /(\d+),(\d+)/ ];
  }

  my $max_distance = 0;
  my ($shop_max_1, $shop_max_2);
  for ( my $i = 0; $i <= $#coords; $i ++ ) {
    for ( my $j = $i+1;  $j <= $#coords; $j ++ ) {
      my $distance = distance( $coords[$i],$coords[$j] );
      if ( $distance > $max_distance ) {
	$max_distance=$distance;
	$shop_max_1 = $i;
	$shop_max_2 = $j;
      }
    }
  }
  print "Max for $_: Distance $max_distance for shops $shop_max_1 and $shop_max_2\n";
}

#------------------------------------------------
sub distance {
  my ($coord_1, $coord_2 ) = @_;
  my $distance_x = $coord_1->[0] - $coord_2->[0];
  my $distance_y = $coord_1->[1] - $coord_2->[1];
  return sqrt($distance_x*$distance_x + $distance_y*$distance_y);
}
